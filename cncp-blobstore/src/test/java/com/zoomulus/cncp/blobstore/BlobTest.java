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

import com.google.common.base.Strings;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.OffsetDateTime;

import static com.zoomulus.cncp.utils.Streams.randomInputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlobTest {
    private BlobStore blobStore;

    @Before
    public void setup() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void testCreateBlob() {
        String blobName = "test blob";
        long blobSize = 1024;
        OffsetDateTime now = OffsetDateTime.now().withNano(0);

        Blob blob = blobStore.createBlob(blobName, blobSize);

        assertFalse(Strings.isNullOrEmpty(blob.getId().toString()));
        assertEquals(blobName, blob.getCommonName());
        assertEquals(blobSize, blob.getLength());
        assertTrue(1 == blob.getCreated().compareTo(now));
    }

    @Test
    public void testGetBlob() throws BlobNotFoundException {
        String blobName = "test blob";
        long blobSize = 1024;
        OffsetDateTime now = OffsetDateTime.now().withNano(0);

        String stringId = blobStore.createBlob(blobName, blobSize).getId().toString();

        Blob blob = blobStore.getBlob(BlobIdentifier.createFromStringId(stringId));

        assertFalse(Strings.isNullOrEmpty(blob.getId().toString()));
        assertEquals(blobName, blob.getCommonName());
        assertEquals(blobSize, blob.getLength());
        assertTrue(1 == blob.getCreated().compareTo(now));
    }

    @Test
    public void testWriteBlob() throws IOException {
        Blob blob = blobStore.createBlob("test blob", 1024);

        blob.write(randomInputStream(1024));

        StringWriter writer = new StringWriter();
        InputStream result = blob.read();
        while (result.available() > 0) {
            writer.write(result.read());
        }
        assertEquals(1024, writer.toString().length());
    }

    @Test
    public void testExists() throws IOException {
        Blob blob = blobStore.createBlob("test blob", 1024);
        assertFalse(blobStore.exists(blob.getId()));
        blob.write(randomInputStream(1024));
        assertTrue(blobStore.exists(blob.getId()));
    }

    @Test
    public void testDeleteBlob() throws IOException {
        Blob blob = blobStore.createBlob("test blob", 1024);
        blob.write(randomInputStream((int) blob.getLength()));
        assertTrue(blobStore.exists(blob.getId()));

        assertTrue(blobStore.delete(blob.getId()));
        assertFalse(blobStore.exists(blob.getId()));

        assertFalse(blobStore.delete(blob.getId()));
    }

    @Test
    public void testWriteDirect() throws IOException {
        Blob blob = blobStore.createBlob("test blob", 1024);
        DirectWriteContext ctx = blob.getDirectWriteContext();
        ctx.write(randomInputStream((int) blob.getLength()));
        blob.endDirectWrite(ctx);

        assertTrue(blobStore.exists(blob.getId()));
    }

    @Test
    public void testURIDownload() throws IOException  {
        Blob blob = blobStore.createBlob("test blob", 1024);
        blob.write(randomInputStream((int) blob.getLength()));

        Blob blob2 = blobStore.getBlob(blob.getId());
        DirectReadContext ctx = blob2.getDirectReadContext();
        StringWriter writer = new StringWriter();
        InputStream is = ctx.getInputStream();
        while (is.available() > 0) {
            writer.write((byte) is.read());
        }

        assertEquals(1024, writer.toString().length());
    }
}
