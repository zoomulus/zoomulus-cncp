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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

public class InMemoryNodeStore implements NodeStore {
    private final Node root;
    private final Map<Node, SortedMap<String, Node>> children = Maps.newConcurrentMap();
    private final Map<Node, Node> parents = Maps.newConcurrentMap();
    private final Map<Node, OffsetDateTime> created = Maps.newConcurrentMap();
    private final Map<Node, OffsetDateTime> lastModified = Maps.newConcurrentMap();
    private final Map<Node, Map<String, Object>> properties = Maps.newConcurrentMap();
    private final Map<Node, Content> content = Maps.newConcurrentMap();

    @Inject
    public InMemoryNodeStore() {
        root = createNode("/");
    }

    private Node createNode(@NotNull final String path) {
        OffsetDateTime now = OffsetDateTime.now();
        return createNode(path, now, now);
    }

    private Node createNode(@NotNull final String path,
                            @NotNull final OffsetDateTime created,
                            @NotNull final OffsetDateTime lastModified) {
        Node node = new Node(this, path);
        children.put(node, new TreeMap<>());
        this.created.put(node, created);
        this.lastModified.put(node, lastModified);
        properties.put(node, Maps.newConcurrentMap());
        return node;
    }

    @Override
    @NotNull
    public Node getRoot() {
        return root;
    }

    @Override
    @NotNull
    public Node getChild(@NotNull final Node parent, @NotNull final String name) throws NodeNotFoundException {
        Node child = children.get(parent).get(name);
        if (null == child) {
            throw new NodeNotFoundException(String.format("No such child node '%s' found at path '%s'", name, parent.getFullName()));
        }
        return child;
    }

    @Override
    @NotNull
    public Node addChild(@NotNull final Node parent, @NotNull final String name) {
        OffsetDateTime now = OffsetDateTime.now();
        Node childNode = createNode(Paths.get(parent.getFullName(), name).toString(), now, now);
        children.get(parent).put(name, childNode);
        parents.put(childNode, parent);
        lastModified.put(parent, now);
        return childNode;
    }

    @Override
    @NotNull
    public Iterator<Node> getChildIterator(@NotNull final Node parent) {
        return children.get(parent).values().iterator();
    }

    @Override
    public boolean deleteChild(@NotNull final Node parent, @NotNull final String name) {
        Node child = children.get(parent).remove(name);
        lastModified.put(parent, OffsetDateTime.now());
        return null != child;
    }

    @Override
    public OffsetDateTime getCreated(@NotNull final Node node) {
        return created.get(node);
    }

    @Override
    public OffsetDateTime getLastModified(@NotNull final Node node) {
        return lastModified.get(node);
    }

    @Override
    @NotNull
    public ImmutableMap<String, Object> getProperties(@NotNull final Node node) {
        return ImmutableMap.copyOf(properties.get(node));
    }

    @Override
    public void setProperty(@NotNull final Node node, @NotNull final String key, @NotNull final Object value) {
        properties.get(node).put(key, value);
        lastModified.put(node, OffsetDateTime.now());
    }

    @Override
    @Nullable
    public Object deleteProperty(@NotNull final Node node, @NotNull final String key) {
        Object deleted = properties.get(node).remove(key);
        if (null != deleted) {
            lastModified.put(node, OffsetDateTime.now());
        }
        return deleted;
    }

    @Override
    @NotNull
    public Optional<Node> getParent(@NotNull final Node node) {
        if (node.equals(root)) {
            return Optional.empty();
        }
        return Optional.ofNullable(parents.get(node));
    }

    @Override
    @NotNull
    public Optional<Content> getContent(@NotNull final Node node) {
        return Optional.ofNullable(content.get(node));
    }

    @Override
    public void setContent(@NotNull final Node node, @NotNull final Content content) {
        this.content.put(node, content);
        lastModified.put(node, OffsetDateTime.now());
    }
}
