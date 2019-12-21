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

package com.zoomulus.cncp.nodestore;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ContentTest {
    private Content content;

    private static final String CONTENT_NAME = "Test Content";
    private static long CONTENT_LENGTH = 1024;

    @Before
    public void setup() {
        content = new Content(CONTENT_NAME, CONTENT_LENGTH);
    }

    @Test
    public void testGetCommonName() {
        assertTrue(CONTENT_NAME.equals(content.getCommonName()));
    }

    @Test
    public void testGetContentLength() {
        assertEquals(CONTENT_LENGTH, content.getContentLength());
    }

    @Test
    public void testGetIdentifier() {
        assertNotNull(content.getIdentifier());
    }

    @Test
    public void testContentType() {
        assertEquals("application/octet-stream", content.getContentType());
        assertEquals("image/jpeg", new Content("1.jpg", CONTENT_LENGTH, "image/jpeg").getContentType());
        assertEquals("image/jpeg", new Content("2.jpg", CONTENT_LENGTH).getContentType());
    }

    @Test
    public void testIdentifiersAreUnique() {
        Set<String> ids = Sets.newHashSet();
        int size = 100000;
        for (int i=0; i<size; i++) {
            ids.add(new Content("", 0).getIdentifier());
        }
        assertEquals(size, ids.size());
    }
}
