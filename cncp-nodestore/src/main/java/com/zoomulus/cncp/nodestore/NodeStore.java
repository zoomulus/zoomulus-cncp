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

import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.Optional;

public interface NodeStore {
    @NotNull Node getRoot();
    @NotNull Node addChild(@NotNull final Node parent, @NotNull final String name);
    @NotNull Node getChild(@NotNull final Node parent, @NotNull final String name) throws NodeNotFoundException;
    @NotNull Iterator<Node> getChildIterator(@NotNull final Node parent);
    boolean deleteChild(@NotNull final Node parent, @NotNull final String name);
    @NotNull OffsetDateTime getCreated(@NotNull final Node node);
    @NotNull OffsetDateTime getLastModified(@NotNull final Node node);
    @NotNull ImmutableMap<String, Object> getProperties(@NotNull final Node node);
    void setProperty(@NotNull final Node node, @NotNull final String key, @NotNull final Object value);
    @Nullable Object deleteProperty(@NotNull final Node node, @NotNull final String key);
    @NotNull Optional<Node> getParent(@NotNull final Node node);
    @NotNull Optional<Content> getContent(@NotNull final Node node);
    void setContent(@NotNull final Node node, @NotNull final Content content);
}
