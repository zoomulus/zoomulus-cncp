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

package com.zoomulus.cncp.blobstore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;

import static com.zoomulus.cncp.utils.Identifiers.generateUniqueIdentifier;

public class Blob {
    private final BlobStore blobStore;
    private final BlobIdentifier id;

    Blob(@NotNull final BlobStore blobStore,
         @NotNull final String name,
         long length) {
        this(blobStore, generateUniqueIdentifier(), name, length);
    }

    Blob(@NotNull final BlobStore blobStore,
         @NotNull final String uniqueId,
         @NotNull final String name,
         long length) {
        this(blobStore, uniqueId, name, length, OffsetDateTime.now());
    }

    Blob(@NotNull final BlobStore blobStore,
         @NotNull final String uniqueId,
         @NotNull final String name,
         long length,
         @NotNull final OffsetDateTime created) {
        this.blobStore = blobStore;
        id = new BlobIdentifier(uniqueId, name, length, created);
    }

    Blob(@NotNull final BlobStore blobStore, @NotNull final BlobIdentifier blobIdentifier) throws IllegalArgumentException {
        this.blobStore = blobStore;
        id = blobIdentifier;
    }

    @NotNull
    public BlobIdentifier getId() {
        return id;
    }

    @NotNull
    public String getCommonName() {
        return id.getCommonName();
    }

    public long getLength() {
        return id.getLength();
    }

    @NotNull
    public OffsetDateTime getCreated() {
        return id.getCreated();
    }

    public void write(@NotNull final InputStream inputStream) throws IOException  {
        blobStore.write(id, inputStream);
    }

    @NotNull
    public InputStream read() throws BlobNotFoundException {
        return blobStore.read(id);
    }

    @Nullable
    public DirectWriteContext getDirectWriteContext() {
        return blobStore.getDirectWriteContext(id);
    }

    public boolean endDirectWrite(@NotNull final DirectWriteContext ctx) {
        return blobStore.endDirectWrite(id, ctx);
    }

    @Nullable
    public DirectReadContext getDirectReadContext() throws BlobNotFoundException {
        return blobStore.getDirectReadContext(id);
    }
}
