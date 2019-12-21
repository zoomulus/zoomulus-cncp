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

import org.junit.Test;

import java.time.OffsetDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BlobIdentifierTest {
    @Test
    public void testConstructFromFields() {
        String id = "id";
        String name = "name";
        long size = 10;
        OffsetDateTime now = OffsetDateTime.now().withNano(0);

        BlobIdentifier blobId = new BlobIdentifier(id, name, size, now);

        assertNotNull(blobId);
        assertEquals(id, blobId.getUniqueId());
        assertEquals(name, blobId.getCommonName());
        assertEquals(size, blobId.getLength());
        assertEquals(now, blobId.getCreated());
    }

    @Test
    public void testConstructFromStringId() {
        String id = "id";
        String name = "name";
        long size = 10;
        OffsetDateTime now = OffsetDateTime.now().withNano(0);

        String stringId = BlobIdentifier.createBlobIdentifier(id, name, 10, now);
        BlobIdentifier blobId = BlobIdentifier.createFromStringId(stringId);

        assertNotNull(blobId);
        assertEquals(id, blobId.getUniqueId());
        assertEquals(name, blobId.getCommonName());
        assertEquals(size, blobId.getLength());
        assertEquals(now, blobId.getCreated());
    }
}
