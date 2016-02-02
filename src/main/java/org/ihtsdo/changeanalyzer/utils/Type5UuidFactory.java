package org.ihtsdo.changeanalyzer.utils;

/**
 * Copyright (c) 2009 International Health Terminology Standards Development
 * Organisation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Type5UuidFactory {

    public static UUID PATH_ID_FROM_FS_DESC = UUID.fromString("5a2e7786-3e41-11dc-8314-0800200c9a66");
    public static UUID REL_GROUP_NAMESPACE = UUID.fromString("8972fef0-ad53-11df-94e2-0800200c9a66");
    public static UUID USER_FULLNAME_NAMESPACE = UUID.fromString("cad85220-1ed4-11e1-8bc2-0800200c9a66");


    public static UUID OPCS_CONCEPT_ID = UUID.fromString("26854022-8bd0-11dc-8314-0800200c9a66");
    public static UUID OPCS_DESC_ID = UUID.fromString("26854023-8bd0-11dc-8314-0800200c9a66");
    public static UUID OPCS_REL_ID = UUID.fromString("26854024-8bd0-11dc-8314-0800200c9a66");

    public static UUID AUTHOR_TIME_ID = UUID.fromString("c6915290-30fc-11e1-b86c-0800200c9a66");

    /*
     * public static UUID READV3_CONCEPT_ID =
     * UUID.fromString("45419916-8ecd-11dc-8314-0800200c9a66");
     * public static UUID READV3_REL_ID =
     * UUID.fromString("45419917-8ecd-11dc-8314-0800200c9a66");
     * public static UUID READV3_TERM30_DESC_ID =
     * UUID.fromString("45419918-8ecd-11dc-8314-0800200c9a66");
     * public static UUID READV3_TERM60_DESC_ID =
     * UUID.fromString("45419919-8ecd-11dc-8314-0800200c9a66");
     * public static UUID READV3_TERM198_DESC_ID =
     * UUID.fromString("4541991a-8ecd-11dc-8314-0800200c9a66");
     *
     * public static UUID READV3_REL_ORDER_REFSET_MEMBER_ID =
     * UUID.fromString("48c30c58-b5ce-11dc-8314-0800200c9a66");
     * public static UUID READV3_LINGUISTIC_ROLE_MEMBER_ID =
     * UUID.fromString("0da484c0-b5d1-11dc-8314-0800200c9a66");
     * public static UUID READV3_SUBJECT_TYPE_ID =
     * UUID.fromString("0da484c1-b5d1-11dc-8314-0800200c9a66");
     *
     * public static UUID READV3_KEY_MEMBER_ID =
     * UUID.fromString("2bd4a1b0-b70b-11dc-8314-0800200c9a66");
     *
     * public static UUID READV3_TEMPL_VALUE_TYPE_MEMBER_ID =
     * UUID.fromString("5acad348-b735-11dc-8314-0800200c9a66");
     * public static UUID READV3_TEMPL_CARDINALITY_MEMBER_ID =
     * UUID.fromString("5acad349-b735-11dc-8314-0800200c9a66");
     * public static UUID READV3_TEMPL_SEMANTIC_STATUS_MEMBER_ID =
     * UUID.fromString("5acad34a-b735-11dc-8314-0800200c9a66");
     * public static UUID READV3_TEMPL_BROWSE_ATTRIBUTE_ORDER_MEMBER_ID =
     * UUID.fromString("5acad34b-b735-11dc-8314-0800200c9a66");
     * public static UUID READV3_TEMPL_BROWSE_VALUE_ORDER_MEMBER_ID =
     * UUID.fromString("5acad34c-b735-11dc-8314-0800200c9a66");
     * public static UUID READV3_TEMPL_NOTES_SCREEN_ORDER_MEMBER_ID =
     * UUID.fromString("5acad34d-b735-11dc-8314-0800200c9a66");
     * public static UUID READV3_TEMPL_ATTRIBUTE_DISPLAYSTATUS_MEMBER_ID =
     * UUID.fromString("5acad34e-b735-11dc-8314-0800200c9a66");
     * public static UUID READV3_TEMPL_CHARACTERISTIC_STATUS_MEMBER_ID =
     * UUID.fromString("5acad34f-b735-11dc-8314-0800200c9a66");
     *
     * public static UUID READV3_TEMPL_FOR_REL =
     * UUID.fromString("bd98b290-bc0d-11dc-95ff-0800200c9a66");
     * public static UUID READV3_TEMPL =
     * UUID.fromString("bd98b291-bc0d-11dc-95ff-0800200c9a66");
     * public static UUID READV3_CROSS_MAP_FOR_REL =
     * UUID.fromString("bd98b292-bc0d-11dc-95ff-0800200c9a66");
     * public static UUID READV3_CROSS_MAP =
     * UUID.fromString("bd98b293-bc0d-11dc-95ff-0800200c9a66");
     */

    /* ICD 10AM UUIDS Added by NCCH */
    public static UUID ICD10AM_CONCEPT_ID = UUID.fromString("fc5b4274-98a2-11dc-b260-b51f56d89593");
    public static UUID ICD10AM_DESC_ID = UUID.fromString("fc5b4275-98a2-11dc-b260-b51f56d89593");
    public static UUID ICD10AM_REL_ID = UUID.fromString("fc5b4276-98a2-11dc-b260-b51f56d89593");

    public static final String encoding = "8859_1";

    public static UUID get(UUID namespace, String name) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest sha1Algorithm = MessageDigest.getInstance("SHA-1");

        // Generate the digest.
        sha1Algorithm.reset();
        if (namespace != null) {
            sha1Algorithm.update(getRawBytes(namespace));
        }
        sha1Algorithm.update(name.getBytes(encoding));
        byte[] sha1digest = sha1Algorithm.digest();

        sha1digest[6] &= 0x0f; /* clear version */
        sha1digest[6] |= 0x50; /* set to version 5 */
        sha1digest[8] &= 0x3f; /* clear variant */
        sha1digest[8] |= 0x80; /* set to IETF variant */

        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (sha1digest[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (sha1digest[i] & 0xff);

        return new UUID(msb, lsb);
    }

    public static UUID get(String name) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return get(null, name);
    }

    /**
     * This routine adapted from org.safehaus.uuid.UUID,
     * which is licensed under Apache 2.
     *
     * @param uid
     * @return
     */
    public static byte[] getRawBytes(UUID uid) {
        String id = uid.toString();
        if (id.length() != 36) {
            throw new NumberFormatException("UUID has to be represented by the standard 36-char representation");
        }
        byte[] rawBytes = new byte[16];

        for (int i = 0, j = 0; i < 36; ++j) {
            // Need to bypass hyphens:
            switch (i) {
                case 8:
                case 13:
                case 18:
                case 23:
                    if (id.charAt(i) != '-') {
                        throw new NumberFormatException("UUID has to be represented by the standard 36-char representation");
                    }
                    ++i;
            }
            char c = id.charAt(i);

            if (c >= '0' && c <= '9') {
                rawBytes[j] = (byte) ((c - '0') << 4);
            } else if (c >= 'a' && c <= 'f') {
                rawBytes[j] = (byte) ((c - 'a' + 10) << 4);
            } else if (c >= 'A' && c <= 'F') {
                rawBytes[j] = (byte) ((c - 'A' + 10) << 4);
            } else {
                throw new NumberFormatException("Non-hex character '" + c + "'");
            }

            c = id.charAt(++i);

            if (c >= '0' && c <= '9') {
                rawBytes[j] |= (byte) (c - '0');
            } else if (c >= 'a' && c <= 'f') {
                rawBytes[j] |= (byte) (c - 'a' + 10);
            } else if (c >= 'A' && c <= 'F') {
                rawBytes[j] |= (byte) (c - 'A' + 10);
            } else {
                throw new NumberFormatException("Non-hex character '" + c + "'");
            }
            ++i;
        }
        return rawBytes;
    }

}

