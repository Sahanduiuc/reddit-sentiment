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
import org.jdbi.v3.core.locator.ClasspathSqlLocator;

import java.io.File;

/**
 * Simple database schema installer
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class InitDatabase {
    public static void main(String[] args) {
        File homeDir = new File(System.getProperty("user.home") + File.separator + ".serenity");
        if (!homeDir.exists()) {
            boolean created = homeDir.mkdirs();
            if (!created) {
                throw new IllegalStateException("unable to create home directory: " + homeDir);
            }
        }

        Jdbi dbi = Jdbi.create("jdbc:sqlite:" + homeDir + File.separatorChar + "sentiment.db");
        String installScript = ClasspathSqlLocator.findSqlOnClasspath(InitDatabase.class.getName() + "_install");
        dbi.useHandle(hnd -> {
            int totalExecuted = 0;
            int[] rowsExecuted = hnd.createScript(installScript).execute();
            for (int rows : rowsExecuted) {
                totalExecuted += rows;
            }
            System.out.println("Executed: " + totalExecuted);
        });

    }
}
