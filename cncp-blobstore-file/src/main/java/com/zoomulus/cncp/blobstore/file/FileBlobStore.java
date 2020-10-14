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

package com.zoomulus.cncp.blobstore.file;

import com.google.common.collect.Maps;
import com.zoomulus.cncp.blobstore.Blob;
import com.zoomulus.cncp.blobstore.BlobIdentifier;
import com.zoomulus.cncp.blobstore.BlobNotFoundException;
import com.zoomulus.cncp.blobstore.BlobStore;
import com.zoomulus.cncp.blobstore.DirectReadContext;
import com.zoomulus.cncp.blobstore.DirectWriteContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

public class FileBlobStore implements BlobStore {
    private Map<String, Blob> blobs = Maps.newConcurrentMap();

    private @NotNull final Path rootPath;

    @Inject
    public FileBlobStore(@NotNull @Named("root.path") final Path rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public @NotNull Blob createBlob(@NotNull String name, long length) {
        Blob b = new Blob(this, name, length);
        blobs.put(b.getId().toString(), b);
        return b;
    }

    @Override
    public @NotNull Blob getBlob(@NotNull BlobIdentifier blobId) throws BlobNotFoundException {
        Blob b = blobs.get(blobId.toString());
        if (null == b) {
            throw new BlobNotFoundException(String.format("No such blob [%s] found", blobId.toString()));
        }
        return b;
    }

    @Override
    public void write(@NotNull BlobIdentifier blobId, @NotNull InputStream inputStream) throws IOException {

    }

    @Override
    public @NotNull InputStream read(@NotNull BlobIdentifier blobId) throws BlobNotFoundException {
        return null;
    }

    @Override
    public boolean exists(@NotNull BlobIdentifier blobId) {
        return false;
    }

    @Override
    public boolean delete(@NotNull BlobIdentifier blobId) {
        return false;
    }

    @Override
    public @Nullable DirectWriteContext getDirectWriteContext(@NotNull BlobIdentifier blobId) {
        return null;
    }

    @Override
    public boolean endDirectWrite(@NotNull BlobIdentifier blobId, @NotNull DirectWriteContext ctx) {
        return false;
    }

    @Override
    public @Nullable DirectReadContext getDirectReadContext(@NotNull BlobIdentifier blobId) throws BlobNotFoundException {
        return null;
    }
}
