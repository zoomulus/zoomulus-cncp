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

public interface BlobStore {
    @NotNull Blob createBlob(@NotNull final String name, long length);
    @NotNull  Blob getBlob(@NotNull final BlobIdentifier blobId) throws BlobNotFoundException;
    void write(@NotNull final BlobIdentifier blobId, @NotNull final InputStream inputStream) throws IOException;
    @NotNull InputStream read(@NotNull final BlobIdentifier blobId) throws BlobNotFoundException;
    boolean exists(@NotNull final BlobIdentifier blobId);
    boolean delete(@NotNull final BlobIdentifier blobId);
    @Nullable DirectWriteContext getDirectWriteContext(@NotNull final BlobIdentifier blobId);
    boolean endDirectWrite(@NotNull final BlobIdentifier blobId, @NotNull final DirectWriteContext ctx);
    @Nullable DirectReadContext getDirectReadContext(@NotNull final BlobIdentifier blobId) throws BlobNotFoundException;
}
