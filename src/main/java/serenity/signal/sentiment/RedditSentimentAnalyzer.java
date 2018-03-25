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

import com.vader.sentiment.analyzer.SentimentAnalyzer;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.tree.CommentNode;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Stream;

/**
 * Pipeline element which takes a stream of comments and transforms it to sentiment scores.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class RedditSentimentAnalyzer {
    private ThreadLocal<SentimentAnalyzer> analyzer = ThreadLocal.withInitial(SentimentAnalyzer::new);

    public Stream<SentimentScore> analyze(Stream<CommentNode<PublicContribution<?>>> textStream) {
        return textStream.map(comment -> {
            String txt = comment.getSubject().getBody();
            if (txt == null) {
                return null;
            }

            SentimentAnalyzer sentimentAnalyzer = analyzer.get();
            sentimentAnalyzer.setInputString(txt);
            try {
                sentimentAnalyzer.setInputStringProperties();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sentimentAnalyzer.analyze();

            String id = comment.getSubject().getId();
            Date created = comment.getSubject().getCreated();
            Float polarity = sentimentAnalyzer.getPolarity().get("compound");
            int weight = comment.getSubject().getScore();

            return new SentimentScore(created, id, polarity, weight);
        });
    }
}
