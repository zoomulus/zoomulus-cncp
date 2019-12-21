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

import org.jetbrains.annotations.NotNull;

import javax.activation.MimetypesFileTypeMap;

import static com.zoomulus.cncp.utils.Identifiers.generateUniqueIdentifier;

public class Content {
    private final String commonName;
    private final String identifier;
    private long contentLength;
    private final String contentType;

    private static MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();

    public Content(@NotNull final String name, long length) {
        this(name, length, mimetypesFileTypeMap.getContentType(name));
    }

    public Content(@NotNull final String name, long length, @NotNull final String mimeType) {
        commonName = name;
        contentLength = length;
        contentType = mimeType;
        identifier = generateUniqueIdentifier();
    }

    @NotNull
    public String getCommonName() {
        return commonName;
    }

    @NotNull
    public long getContentLength() {
        return contentLength;
    }

    @NotNull
    public String getContentType() {
        return contentType;
    }

    @NotNull
    public String getIdentifier() {
        return identifier;
    }
}
