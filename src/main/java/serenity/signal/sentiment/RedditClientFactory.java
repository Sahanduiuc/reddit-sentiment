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

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Hook to make the signal generator testable -- replace this with a mock source of Reddit data.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
class RedditClientFactory {
    private final String redditUser;
    private final String clientId;
    private final String secret;

    RedditClientFactory(@Nonnull String redditUser, @Nonnull String clientId, @Nonnull String secret) {
        this.redditUser = redditUser;
        this.clientId = clientId;
        this.secret = secret;
    }

    RedditClient createClient() {
        Credentials oauthCreds = Credentials.userless(clientId, secret, UUID.randomUUID());
        UserAgent userAgent = new UserAgent("Serenity", "serenity-signals", "1.0", redditUser);
        return OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), oauthCreds);
    }
}
