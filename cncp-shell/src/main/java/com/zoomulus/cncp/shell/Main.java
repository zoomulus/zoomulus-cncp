/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zoomulus.cncp.shell;

import com.google.inject.Guice;
import com.zoomulus.cli.CLI;
import com.zoomulus.cncp.shell.modules.AppModule;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;

public class Main {
    private static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(@NotNull final String[] args) {
        try {
            Options options = new Options();
            options.addOption("c","config",true,"Configuration file path (default: ~/.zoomsh/config");

            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(options, args);

            File cfg = commandLine.hasOption("config") ?
                    new File(commandLine.getOptionValue("config")) :
                    Paths.get(System.getProperty("user.home"), ".zoomsh", "config").toFile();

            if(cfg.exists()) {
                Guice.createInjector(new AppModule(cfg)).getInstance(CLI.class)
                        .withPrompt("=zoomsh > ")
                        .findCommandsRecursive(Main.class.getPackageName())
                        .run();
            }
            else {
                LOG.error("No configuration file found at {}", cfg.getAbsolutePath());
            }
        }
        catch (ParseException e) {
            LOG.error("Unable to parse command line", e);
        }
        catch (Exception e) {
            LOG.error("Caught exception", e);
        }
    }
}
