/*
 * (C) Copyright 2017 Kyle F. Downey.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package serenity.signal.sentiment;

import cloudwall.appconfig.Blade;
import com.google.common.collect.Streams;
import dagger.Component;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.SearchPaginator;
import net.dean.jraw.references.SubmissionReference;
import net.dean.jraw.tree.CommentNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * A signal generator based off sentiment analysis of Reddit posts.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class RedditSignalGenerator implements Blade {
    private static final Logger log = LogManager.getLogger(RedditSignalGenerator.class);

    private final RedditClientFactory clientFactory;
    private final SentimentScoreProcessor processor;

    @Inject
    public RedditSignalGenerator(@Nonnull RedditClientFactory clientFactory, SentimentScoreProcessor processor) {
        this.clientFactory = clientFactory;
        this.processor = processor;
    }

    @Override
    public void run() {
        log.info("starting up Reddit sentiment signal generator");

        RedditClient reddit = clientFactory.createClient();
        SearchPaginator search = reddit.subreddit("BitcoinMarkets")
                .search()
                .timePeriod(TimePeriod.ALL)
                .query("[Daily Discussion]")
                .build();

        RedditSentimentAnalyzer analyzer = new RedditSentimentAnalyzer();
        long startTime = System.currentTimeMillis();
        AtomicInteger commentCount = new AtomicInteger();
        search.forEach(listing -> listing.getChildren().parallelStream()
                .forEach(submission -> {
                    SubmissionReference ref = submission.toReference(reddit);
                    Iterator<CommentNode<PublicContribution<?>>> commentIter = ref.comments().walkTree().iterator();
                    Stream<CommentNode<PublicContribution<?>>> comments = Streams.stream(commentIter);
                    Stream<SentimentScore> scores = analyzer.analyze(comments);

                    scores.forEach(score -> {
                        if (score != null) {
                            commentCount.incrementAndGet();
                            processor.accept(score);
                        }
                    });
                }));

        Duration processingTime = Duration.ofMillis(System.currentTimeMillis() - startTime);
        long minutes = processingTime.toMinutes();
        long seconds = processingTime.minusMinutes(minutes).getSeconds();
        log.info("Analyzed {} comments in {} minutes and {} seconds", commentCount.get(), minutes, seconds);
    }

    @Singleton
    @Component(modules = RedditSignalGeneratorModule.class)
    public interface SignalComponent {
        @SuppressWarnings("unused")
        RedditSignalGenerator signalGenerator();
    }
}
