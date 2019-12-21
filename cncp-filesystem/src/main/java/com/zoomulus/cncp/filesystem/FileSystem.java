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

package com.zoomulus.cncp.filesystem;

import com.zoomulus.cncp.blobstore.BlobStore;
import com.zoomulus.cncp.nodestore.NodeStore;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Paths;

public class FileSystem {
    private String cwd = File.separator;
    private final NodeStore nodeStore;
    private final BlobStore blobStore;

    @Inject
    public FileSystem(@NotNull final NodeStore nodeStore, @NotNull final BlobStore blobStore) {
        this.nodeStore = nodeStore;
        this.blobStore = blobStore;
    }

    public String getCwd() {
        return cwd;
    }

    public void cd(@NotNull final String newDir) {
        if (newDir.startsWith(File.separator)) {
            cwd = newDir;
        }
        else {
            cwd = Paths.get(cwd, newDir).toString();
        }
    }
}
