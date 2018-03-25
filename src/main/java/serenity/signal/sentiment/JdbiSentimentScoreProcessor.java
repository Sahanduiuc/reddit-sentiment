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

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.stringtemplate4.StringTemplateSqlLocator;
import org.stringtemplate.v4.ST;

import java.util.HashSet;
import java.util.Set;

/**
 * Processor which loads unique comment sentiment scores into a SQLLite database for later analysis.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class JdbiSentimentScoreProcessor implements SentimentScoreProcessor {
    private static final String ST_GROUP = "serenity/signal/sentiment/JdbiSentimentScoreProcessor.sql.stg";

    private final Jdbi dbi;

    private final ST queryTemplate;
    private final ST insertTemplate;

    private final Set<String> knownIds = new HashSet<>();

    public JdbiSentimentScoreProcessor(Jdbi dbi) {
        this.dbi = dbi;
        this.queryTemplate = StringTemplateSqlLocator.findStringTemplate(ST_GROUP, "selectAll");
        this.insertTemplate = StringTemplateSqlLocator.findStringTemplate(ST_GROUP, "insert");

        dbi.useHandle(handle -> {
            String sql = queryTemplate.render();
            handle.createQuery(sql).map((rs, ctx) -> rs.getString("id")).forEach(knownIds::add);
        });
    }

    @Override
    public void accept(SentimentScore sentimentScore) {
        if (knownIds.contains(sentimentScore.getId())) {
            return;
        }
        dbi.useHandle(handle -> {
            String sql = insertTemplate.render();
            handle.createUpdate(sql)
                    .bind("id", sentimentScore.getId())
                    .bind("polarity", sentimentScore.getPolarity())
                    .bind("weight", sentimentScore.getWeight())
                    .bind("createdTs", sentimentScore.getTimestamp())
            .execute();
        });
    }
}
