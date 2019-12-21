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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

public class InMemoryBlobStore implements BlobStore {
    private static Logger LOG = LoggerFactory.getLogger(InMemoryBlobStore.class);

    private final byte[] secretKey = UUID.randomUUID().toString().getBytes();

    private Map<String, Blob> blobs = Maps.newConcurrentMap();
    private Map<String, ByteBuffer> store = Maps.newConcurrentMap();

    @Override
    @NotNull
    public Blob createBlob(@NotNull final String name, long length) {
        Blob blob = new Blob(this, name, length);
        blobs.put(blob.getId().toString(), blob);
        return blob;
    }

    @Override
    @NotNull
    public Blob getBlob(@NotNull final BlobIdentifier blobId) throws BlobNotFoundException {
        Blob blob = blobs.get(blobId.toString());
        if (null == blob) {
            throw new BlobNotFoundException(String.format("No blob found for id '%s'", blobId.toString()));
        }
        return blob;
    }

    @Override
    public void write(@NotNull final BlobIdentifier blobId, @NotNull final InputStream inputStream) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate((int) blobId.getLength());
        int ctr = 0;
        while (inputStream.available() > 0) {
            buffer.put((byte) inputStream.read());
            ctr++;
        }
        buffer.flip();
        store.put(blobId.toString(), buffer);
    }

    @Override
    @NotNull
    public InputStream read(@NotNull final BlobIdentifier blobId) throws BlobNotFoundException {
        if (! store.containsKey(blobId.toString())) {
            throw new BlobNotFoundException(String.format("No blob found for id '%s'", blobId.toString()));
        }
        ByteBuffer buffer = store.get(blobId.toString());
        InputStream is = new BufferedInputStream(new ByteArrayInputStream(buffer.array()));
        buffer.flip();
        return is;
    }

    @Override
    public boolean exists(@NotNull final BlobIdentifier blobId) {
        return store.containsKey(blobId.toString());
    }

    @Override
    public boolean delete(@NotNull final BlobIdentifier blobId) {
        if (blobs.containsKey(blobId.toString())) {
            store.remove(blobId.toString());
            blobs.remove(blobId.toString());
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public DirectWriteContext getDirectWriteContext(@NotNull final BlobIdentifier blobId) {
        String uploadId = UUID.randomUUID().toString();
        try {
            return new InMemoryDirectWriteContext(
                    new DirectWriteToken(blobId.toString(), uploadId).encode(getBlobStoreSecretKey()),
                    Lists.newArrayList(),
                    blobId.getLength(),
                    blobId.getLength()
            );
        }
        catch (DirectWriteToken.SigningException e) {
            LOG.warn("Unable to sign upload token", e);
        }
        return null;
    }

    @Override
    public boolean endDirectWrite(@NotNull final BlobIdentifier blobId, @NotNull final DirectWriteContext ctx) {
        try {
            DirectWriteToken token = DirectWriteToken.fromEncodedString(ctx.getEncodedToken(), getBlobStoreSecretKey());
            if (! blobId.toString().equals(token.getBlobId())) {
                LOG.warn("Tokens don't match");
                return false;
            }
            write(blobId, ((InMemoryDirectWriteContext) ctx).getInputStream());
        }
        catch (IOException | IllegalArgumentException | DirectWriteToken.SigningException e) {
            LOG.warn("Unable to complete direct write", e);
        }
        return false;
    }

    @Override
    @Nullable
    public DirectReadContext getDirectReadContext(@NotNull final BlobIdentifier blobId) throws BlobNotFoundException {
        return new InMemoryDirectReadContext(read(blobId));
    }

    private byte[] getBlobStoreSecretKey() {
        return secretKey;
    }
}
