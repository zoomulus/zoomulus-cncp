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

package com.zoomulus.cncp.shell.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.zoomulus.cncp.blobstore.BlobStore;
import com.zoomulus.cncp.nodestore.InMemoryNodeStore;
import com.zoomulus.cncp.nodestore.NodeStore;
import com.zoomulus.cncp.shell.Config;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppModule extends AbstractModule {
    private final Config cfg;
    private NodeStore nodeStore = null;
    private BlobStore blobStore;

    public AppModule(@NotNull final File cfgFile) throws IOException {
        cfg = new Config(cfgFile);
    }

    @Override
    public void configure() {
        bind(Config.class).toInstance(cfg);
        bind(String.class).annotatedWith(Names.named("cfg.path")).toInstance(cfg.getCfgPath().toString());
        for (Object o : cfg.keySet()) {
            String key = (String) o;
            bind(String.class).annotatedWith(Names.named(key)).toInstance(cfg.getProperty(key));
        }
    }

    @Provides
    public NodeStore getNodeStore() {
        if (null == nodeStore) {
            nodeStore = new InMemoryNodeStore();

        }
        return nodeStore;
    }

    private Path getRootPath() {
        return Paths.get(cfg.getProperty("root.path", Paths.get(cfg.getCfgPath().toFile().getParent(), "blobStore").toString()));
    }
}
