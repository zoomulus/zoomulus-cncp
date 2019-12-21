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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NodeTest {
    private Node root;

    @Before
    public void createRootNode() {
        NodeStore nodeStore = new InMemoryNodeStore();
        root = nodeStore.getRoot();
    }

    @Test
    public void testAddChild() throws NodeNotFoundException {
        assertTrue(root.isLeaf());
        Node child = root.addChild("c1");
        assertNotNull(child);
        assertFalse(root.isLeaf());
        assertEquals(child, root.getChild("c1"));
    }

    @Test
    public void testIsLeaf() {
        assertTrue(root.isLeaf());

        root.addChild("c1");

        assertFalse(root.isLeaf());
    }

    @Test
    public void testHasChildren() {
        assertFalse(root.hasChildren());

        root.addChild("c1");

        assertTrue(root.hasChildren());
    }

    @Test
    public void testGetChild() throws NodeNotFoundException {
        try {
            root.getChild("c1");
            fail("Expected NodeNotFoundException");
        }
        catch (NodeNotFoundException e) { }

        Node childNode = root.addChild("c1");
        assertEquals(childNode, root.getChild("c1"));
    }

    @Test
    public void testGetChildren() throws NodeNotFoundException {
        Set<String> nodeNames = Sets.newHashSet("1", "2", "3", "4", "5");
        for (String nodeName : nodeNames) {
            root.addChild(nodeName);
        }

        int ctr = 0;
        Iterator<Node> childIterator = root.getChildIterator();
        while (childIterator.hasNext()) {
            ctr++;
            assertTrue(nodeNames.contains(childIterator.next().getSimpleName()));
        }

        assertEquals(nodeNames.size(), ctr);
    }

    @Test
    public void testGetChildrenSorted() throws NodeNotFoundException {
        List<String> nodeNames = Lists.newArrayList("alphabet", "1", "after", "2", "alphabetic", "3", "Before");
        for (String nodeName : nodeNames) {
            root.addChild(nodeName);
        }

        nodeNames.sort(Comparator.naturalOrder());

        int ctr = 0;
        Iterator<Node> childIterator = root.getChildIterator();
        while (childIterator.hasNext()) {
            String childNodeName = childIterator.next().getSimpleName();
            String expectedName = nodeNames.get(ctr++);
            assertTrue(childNodeName.equals(expectedName));
        }
    }

    @Test
    public void testDeleteChild() throws NodeNotFoundException {
        assertFalse(root.deleteChild("c1"));

        assertNotNull(root.addChild("c1"));
        assertTrue(root.hasChildren());
        assertNotNull(root.getChild("c1"));

        assertTrue(root.deleteChild("c1"));
        assertFalse(root.hasChildren());
    }

    @Test
    public void testCreated() throws NodeNotFoundException {
        OffsetDateTime before = OffsetDateTime.now();
        OffsetDateTime created = root.addChild("c1").getCreated();
        OffsetDateTime after = OffsetDateTime.now();
        assertTrue(1 == created.compareTo(before));
        assertTrue(-1 == created.compareTo(after));
    }

    @Test
    public void testLastModified() throws NodeNotFoundException {
        Node child = root.addChild("c1");
        assertTrue(0 == child.getCreated().compareTo(child.getLastModified()));
    }

    @Test
    public void testLastModifiedChangesOnAddChild() throws NodeNotFoundException {
        OffsetDateTime rootLastModified = root.getLastModified();
        root.addChild("c1");
        assertTrue(-1 == rootLastModified.compareTo(root.getLastModified()));
    }

    @Test
    public void testLastModifiedChangesOnDeleteChild() throws NodeNotFoundException {
        OffsetDateTime rootLastModified = root.addChild("c1").getLastModified();
        root.deleteChild("c1");
        assertTrue(-1 == rootLastModified.compareTo(root.getLastModified()));
    }

    @Test
    public void testLastModifiedDoesntChangeOnGetters() throws NodeNotFoundException {
        root.addChild("c1");
        OffsetDateTime rootLastModified = root.getLastModified();
        Node child = root.getChild("c1");
        assertTrue(0 == rootLastModified.compareTo(root.getLastModified()));
        Iterator childIterator = root.getChildIterator();
        assertTrue(0 == rootLastModified.compareTo(root.getLastModified()));
        while (childIterator.hasNext()) { childIterator.next(); }
        assertTrue(0 == rootLastModified.compareTo(root.getLastModified()));
        root.isLeaf();
        assertTrue(0 == rootLastModified.compareTo(root.getLastModified()));
        root.hasChildren();
        assertTrue(0 == rootLastModified.compareTo(root.getLastModified()));
    }

    @Test
    public void testSetProperty() {
        root.setProperty("key", "value");
        assertFalse(root.getProperties().isEmpty());

        String value = (String) root.getProperties().get("key");
        assertTrue(value.equals("value"));

        root.setProperty("int", 5);
        assertEquals(5, (int) root.getProperties().get("int"));
    }

    @Test
    public void testGetPropertiesIsInitiallyEmpty() {
        assertTrue(root.getProperties().isEmpty());
    }

    @Test
    public void testDeleteProperty() {
        root.setProperty("key", "value");
        assertFalse(root.getProperties().isEmpty());

        assertNotNull(root.deleteProperty("key"));
        assertTrue(root.getProperties().isEmpty());

        assertNull(root.deleteProperty("key"));
    }

    @Test
    public void testAddPropertyUpdatesLastModified() {
        OffsetDateTime rootLastModified = root.getLastModified();
        root.setProperty("key", "value");
        assertTrue(-1 == rootLastModified.compareTo(root.getLastModified()));
    }

    @Test
    public void testDeletePropertyUpdatesLastModified() {
        root.setProperty("key", "value");
        OffsetDateTime rootLastModified = root.getLastModified();
        assertNotNull(root.deleteProperty("key"));
        assertTrue(-1 == rootLastModified.compareTo(root.getLastModified()));
    }

    @Test
    public void testGetPropertiesDoesntChangeLastModified() {
        root.setProperty("key", "value");
        OffsetDateTime rootLastModified = root.getLastModified();
        root.getProperties();
        assertTrue(0 == rootLastModified.compareTo(root.getLastModified()));
    }

    @Test
    public void testGetParent() {
        Node child = root.addChild("c1");
        assertTrue(root.getParent().isEmpty());
        assertFalse(child.getParent().isEmpty());
        assertEquals(root, child.getParent().get());
    }

    @Test
    public void testGetNodeContentEmpty() {
        assertTrue(root.getContent().isEmpty());
    }

    @Test
    public void testGetNodeContent() {
        Content content = new Content("TestContent", 0);
        root.setContent(content);
        assertFalse(root.getContent().isEmpty());
        assertEquals(content, root.getContent().get());
    }

    @Test
    public void testSetContentUpdatesLastModified() {
        OffsetDateTime lastModified = root.getLastModified();
        root.setContent(new Content("TestContent", 0));
        assertTrue(-1 == lastModified.compareTo(root.getLastModified()));
    }
}
