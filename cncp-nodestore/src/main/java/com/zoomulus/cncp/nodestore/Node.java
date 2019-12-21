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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.Optional;

public class Node {
    private final NodeStore nodeStore;
    private final String path;

    Node(@NotNull final NodeStore nodeStore, @NotNull final String path) {
        this.nodeStore = nodeStore;
        this.path = path;
    }

    @NotNull
    public String getSimpleName() {
        return Paths.get(path).getFileName().toString();
    }

    @NotNull
    public String getFullName() {
        return path;
    }

    public boolean isLeaf() {
        return ! hasChildren();
    }

    public boolean hasChildren() {
        return nodeStore.getChildIterator(this).hasNext();
    }

    @NotNull
    public Node addChild(@NotNull final String name) {
        return nodeStore.addChild(this, name);
    }

    @NotNull
    public Node getChild(@NotNull final String name) throws NodeNotFoundException {
        return nodeStore.getChild(this, name);
    }

    @NotNull
    public Iterator<Node> getChildIterator() {
        return nodeStore.getChildIterator(this);
    }

    public boolean deleteChild(@NotNull final String name) {
        return nodeStore.deleteChild(this, name);
    }

    @NotNull
    public OffsetDateTime getCreated() {
        return nodeStore.getCreated(this);
    }

    @NotNull
    public OffsetDateTime getLastModified() {
        return nodeStore.getLastModified(this);
    }

    @NotNull
    public ImmutableMap<String, Object> getProperties() {
        return nodeStore.getProperties(this);
    }

    @NotNull
    public void setProperty(@NotNull final String key, @NotNull final Object value) {
        nodeStore.setProperty(this, key, value);
    }

    @Nullable
    public Object deleteProperty(@NotNull final String key) {
        return nodeStore.deleteProperty(this, key);
    }

    @NotNull
    public Optional<Node> getParent() {
        return nodeStore.getParent(this);
    }

    @NotNull
    public Optional<Content> getContent() {
        return nodeStore.getContent(this);
    }

    public void setContent(@NotNull final Content content) {
        nodeStore.setContent(this, content);
    }
}
