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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class BlobIdentifier {
    private final String uniqueId;
    private final String commonName;
    private long length;
    private OffsetDateTime created;
    private final String identifier;

    static final String DELIM="#!#";

    public BlobIdentifier(@NotNull final String commonName, long length) {
        this(UUID.randomUUID().toString(), commonName, length, OffsetDateTime.now());
    }

    public BlobIdentifier(@NotNull final String uniqueId,
                          @NotNull final String commonName,
                          long length,
                          OffsetDateTime created) {
        this.uniqueId = uniqueId;
        this.commonName = commonName;
        this.length = length;
        this.created = created;
        this.identifier = createBlobIdentifier(uniqueId, commonName, length, created);
    }

    public static BlobIdentifier createFromStringId(@NotNull final String stringId) throws IllegalArgumentException {
        String decoded = new String(Base64.getDecoder().decode(stringId));
        List<String> parts = Splitter.on(DELIM).splitToList(decoded);
        if (parts.size() < 4) {
            throw new IllegalArgumentException(String.format("Cannot construct Blob from identifier '%s' - decoded identifier '%s' does not contain all required fields"));
        }

        return new BlobIdentifier(parts.get(0), parts.get(1), Long.parseLong(parts.get(2)),
                Instant.ofEpochMilli(Long.parseLong(parts.get(3))).atOffset(OffsetDateTime.now().getOffset()));
    }

    static String createBlobIdentifier(@NotNull final String id, @NotNull final String name, long length, OffsetDateTime created) {
        return Base64.getEncoder().encodeToString(
                Joiner.on(DELIM).join(
                        id,
                        name,
                        String.valueOf(length),
                        String.valueOf(created.toInstant().toEpochMilli())
                ).getBytes());
    }

    @Override
    @NotNull
    public String toString() {
        return identifier;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getCommonName() {
        return commonName;
    }

    public long getLength() {
        return length;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
