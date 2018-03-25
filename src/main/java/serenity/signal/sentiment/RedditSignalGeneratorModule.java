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

import cloudwall.appconfig.ConfigModule;
import com.typesafe.config.Config;
import dagger.Module;
import dagger.Provides;
import org.jdbi.v3.core.Jdbi;

import java.io.File;

/**
 * Dagger module for assembling a complete signal generator application.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
@Module(includes = ConfigModule.class)
class RedditSignalGeneratorModule {
    @Provides
    public RedditClientFactory redditClientFactory(Config config) {
        String redditUser = config.getString("redditClientFactory.userName");
        String clientId = config.getString("redditClientFactory.apiClientId");
        String secret = config.getString("redditClientFactory.apiSecret");

        return new RedditClientFactory(redditUser, clientId, secret);
    }

    @Provides
    public SentimentScoreProcessor processor() {
        return new JdbiSentimentScoreProcessor(Jdbi.create("jdbc:sqlite:" + System.getProperty("user.home") +
                File.separator + ".serenity" + File.separator + "sentiment.db"));
    }
}
