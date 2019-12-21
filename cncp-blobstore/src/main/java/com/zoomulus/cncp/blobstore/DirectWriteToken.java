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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DirectWriteToken {
    private static Logger LOG = LoggerFactory.getLogger(DirectWriteToken.class);

    private @NotNull final String blobId;
    private @NotNull final String uploadId;

    public DirectWriteToken(@NotNull final String blobId, @NotNull final String uploadId) {
        this.blobId = blobId;
        this.uploadId = uploadId;
    }

    public static DirectWriteToken fromEncodedString(@NotNull final String encodedToken, @NotNull final byte[] secret)
            throws IllegalArgumentException, SigningException {
        final String[] parts = encodedToken.split("#", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid upload token");
        }

        final String toBeDecoded = parts[0];
        final String expectedSig = parts[1];
        final String actualSig = sign(toBeDecoded, secret);
        if (!expectedSig.equals(actualSig)) {
            throw new IllegalArgumentException("Invalid upload token");
        }

        String decoded = new String(Base64.getDecoder().decode(toBeDecoded));
        String decodedParts[] = decoded.split("#");
        if (decodedParts.length < 2) {
            throw new IllegalArgumentException("Invalid upload token");
        }

        return new DirectWriteToken(decodedParts[0], decodedParts[1]);

    }

    @NotNull
    public String encode(@NotNull final byte[] secret) throws SigningException {
        String preSigned = Base64.getEncoder().encodeToString(Joiner.on("#").join(blobId, uploadId).getBytes());
        String signature = sign(preSigned, secret);
        return Joiner.on("#").join(preSigned, signature);
    }

    @NotNull
    private static String sign(@NotNull final String preSigned, @NotNull final byte[] secret) throws SigningException {
        try {
            final String algorithm = "HmacSHA1";
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(secret, algorithm));
            byte[] hash = mac.doFinal(preSigned.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        }
        catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOG.warn("Could not sign upload token", e);
            throw new SigningException(e);
        }
    }

    @NotNull
    public String getBlobId() {
        return blobId;
    }

    @NotNull
    public String getUploadId() {
        return uploadId;
    }

    public static class SigningException extends Exception {
        public SigningException(@NotNull final Throwable t) {
            super(t);
        }
    }
}
