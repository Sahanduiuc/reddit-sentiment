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

import java.util.Date;

/**
 * Data item generated for each comment.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
class SentimentScore {
    private final Date timestamp;
    private final String id;
    private final float polarity;
    private final int weight;

    SentimentScore(Date timestamp, String id, float polarity, int weight) {
        this.timestamp = timestamp;
        this.id = id;
        this.polarity = polarity;
        this.weight = weight;
    }

    Date getTimestamp() {
        return timestamp;
    }

    String getId() {
        return id;
    }

    float getPolarity() {
        return polarity;
    }

    int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "SentimentScore{" +
                "timestamp=" + timestamp +
                ", id='" + id + '\'' +
                ", polarity=" + polarity +
                ", weight=" + weight +
                '}';
    }
}
