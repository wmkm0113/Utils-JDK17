/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
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
package org.nervousync.utils;

import java.io.*;
import java.lang.Character.UnicodeBlock;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.core.RegexGlobals;
import org.nervousync.exceptions.zip.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nervousync.commons.core.Globals;
import org.nervousync.enumerations.xml.DataType;
import org.nervousync.huffman.HuffmanNode;
import org.nervousync.huffman.HuffmanObject;
import org.nervousync.huffman.HuffmanTree;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * The type String utils.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2010 3:53:41 PM $
 */
public final class StringUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

    private static final String TOP_PATH = "..";

    private static final String CURRENT_PATH = ".";
    private static final int MASK_BYTE_UNSIGNED = 0xFF;
    private static final int PADDING = '=';

    private static final String BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final String BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final String AUTHORIZATION_CODE_ITEMS = "23456789ABCEFGHJKLMNPQRSTUVWXYZ";

    private static final String CHN_ID_CARD_CODE = "0123456789X";
    private static final String CHN_SOCIAL_CREDIT_CODE = "0123456789ABCDEFGHJKLMNPQRTUWXY";

    private static final List<DataType> SIMPLE_DATA_TYPES =
            Arrays.asList(DataType.NUMBER, DataType.STRING, DataType.BOOLEAN, DataType.DATE);
    private static final String SCHEMA_MAPPING_RESOURCE_PATH = "META-INF/nervousync.schemas";
    private static final Map<String, String> SCHEMA_MAPPING = new HashMap<>();

    static {
        schemaMapping();
    }

    private static void schemaMapping() {
        try {
            ClassLoader.getSystemResources(SCHEMA_MAPPING_RESOURCE_PATH)
                    .asIterator()
                    .forEachRemaining(url -> {
                        String basePath = url.getPath();
                        ConvertUtils.propertiesToMap(url, new HashMap<>())
                                .forEach((key, value) ->
                                        SCHEMA_MAPPING.put(key,
                                                StringUtils.replace(basePath, SCHEMA_MAPPING_RESOURCE_PATH, value)));
                    });
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Load schema mapping error! ", e);
            }
        }
    }

    private StringUtils() {
    }

    /**
     * Encode byte arrays using Base32 and not pending the padding character
     * Note: Will return zero length string for given byte arrays is null or arrays length is 0.
     * <pre>
     * StringUtils.base32Encode(null) = ""
     * StringUtils.base32Encode([]) = ""
     * StringUtils.base32Encode([72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100]) = "JBSWY3DPEBLW64TMMQ"
     * </pre>
     *
     * @param bytes byte arrays
     * @return Encoded base32 string
     */
    public static String base32Encode(final byte[] bytes) {
        return base32Encode(bytes, Boolean.FALSE);
    }

    /**
     * Encode byte arrays using Base32
     * Note: Will return zero length string for given byte arrays is null or arrays length is 0.
     * <pre>
     * StringUtils.base32Encode(null, true) = ""
     * StringUtils.base32Encode([], true) = ""
     * StringUtils.base32Encode([72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100], true) = "JBSWY3DPEBLW64TMMQ=="
     * </pre>
     *
     * @param bytes   byte arrays
     * @param padding append padding character if needed
     * @return Encoded base32 string
     */
    public static String base32Encode(final byte[] bytes, boolean padding) {
        if (bytes == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0, index = 0;
        int currentByte, nextByte, digit;

        while (i < bytes.length) {
            currentByte = bytes[i] >= 0 ? bytes[i] : bytes[i] + 256;

            if (index > 3) {
                if ((i + 1) < bytes.length) {
                    nextByte = bytes[i + 1] >= 0 ? bytes[i + 1] : bytes[i + 1] + 256;
                } else {
                    nextByte = 0;
                }

                digit = currentByte & (MASK_BYTE_UNSIGNED >> index);
                index = (index + 5) % 8;
                digit = (digit << index) | nextByte >> (8 - index);
                i++;
            } else {
                digit = (currentByte >> (8 - (index + 5))) & 0x1F;
                index = (index + 5) % 8;
                if (index == 0) {
                    i++;
                }
            }
            stringBuilder.append(BASE32.charAt(digit));
        }

        if (padding) {
            while (stringBuilder.length() % 5 > 0) {
                stringBuilder.append((char) PADDING);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Convert given base32 string to byte array
     * Note: Will return a zero-length array for given base64 string is null or string length is 0.
     * <pre>
     * StringUtils.base32Decode(null) = []
     * StringUtils.base32Decode("") = []
     * StringUtils.base32Decode("JBSWY3DPEBLW64TMMQ") = [72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100]
     * </pre>
     *
     * @param string Encoded base32 string
     * @return Decode byte arrays
     */
    public static byte[] base32Decode(String string) {
        if (string == null || string.length() == 0) {
            return new byte[0];
        }

        while (string.charAt(string.length() - 1) == PADDING) {
            string = string.substring(0, string.length() - 1);
        }

        byte[] bytes = new byte[string.length() * 5 / 8];
        int index = 0;
        StringBuilder stringBuilder = new StringBuilder(8);
        StringBuilder temp;
        for (String c : string.split("")) {
            if (BASE32.contains(c)) {
                int current = BASE32.indexOf(c);
                temp = new StringBuilder(5);
                for (int i = 0; i < 5; i++) {
                    temp.append(current & 1);
                    current >>>= 1;
                }
                temp.reverse();
                if (stringBuilder.length() >= 3) {
                    int currentLength = 8 - stringBuilder.length();
                    stringBuilder.append(temp.substring(0, currentLength));
                    bytes[index] = (byte) Integer.valueOf(stringBuilder.toString(), 2).intValue();
                    index++;
                    stringBuilder = new StringBuilder(8);
                    stringBuilder.append(temp.substring(currentLength));
                } else {
                    stringBuilder.append(temp);
                }
            }
        }
        return bytes;
    }

    /**
     * Encode byte arrays using Base64
     * Note: Will return zero length string for given byte arrays is null or arrays length is 0.
     * <pre>
     * StringUtils.base64Encode(null) = ""
     * StringUtils.base64Encode([]) = ""
     * StringUtils.base64Encode([72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100]) = "SGVsbG8gV29ybGQ="
     * </pre>
     *
     * @param bytes byte arrays
     * @return Encoded base64 string
     */
    public static String base64Encode(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        int length = bytes.length;
        byte[] tempBytes;
        if (length % 3 == 0) {
            tempBytes = bytes;
        } else {
            while (length % 3 != 0) {
                length++;
            }
            tempBytes = new byte[length];
            System.arraycopy(bytes, 0, tempBytes, 0, bytes.length);
            for (int i = bytes.length; i < length; i++) {
                tempBytes[i] = 0;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        while ((index * 3) < length) {
            stringBuilder.append(BASE64.charAt((tempBytes[index * 3] >> 2) & 0x3F));
            stringBuilder.append(BASE64.charAt(((tempBytes[index * 3] << 4)
                    | ((tempBytes[index * 3 + 1] & MASK_BYTE_UNSIGNED) >> 4)) & 0x3F));
            if (index * 3 + 1 < bytes.length) {
                stringBuilder.append(BASE64.charAt(((tempBytes[index * 3 + 1] << 2)
                        | ((tempBytes[index * 3 + 2] & MASK_BYTE_UNSIGNED) >> 6)) & 0x3F));
            }
            if (index * 3 + 2 < bytes.length) {
                stringBuilder.append(BASE64.charAt(tempBytes[index * 3 + 2] & 0x3F));
            }
            index++;
        }

        while (stringBuilder.length() % 3 > 0) {
            stringBuilder.append((char) PADDING);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert given base64 string to byte array
     * Note: Will return the zero-length arrays for given base64 string is null or string length is 0.
     * <pre>
     * StringUtils.base64Decode(null) = []
     * StringUtils.base64Decode("") = []
     * StringUtils.base64Decode("SGVsbG8gV29ybGQ=") = [72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100]
     * </pre>
     *
     * @param string Encoded base64 string
     * @return Decode byte arrays
     */
    public static byte[] base64Decode(final String string) {
        if (StringUtils.isEmpty(string)) {
            return new byte[0];
        }
        String origString = string;
        while (origString.charAt(origString.length() - 1) == PADDING) {
            origString = origString.substring(0, origString.length() - 1);
        }

        byte[] bytes = new byte[origString.length() * 3 / 4];

        int index = 0;
        for (int i = 0; i < origString.length(); i += 4) {
            int index1 = BASE64.indexOf(origString.charAt(i + 1));
            bytes[index * 3] = (byte) (((BASE64.indexOf(origString.charAt(i)) << 2) | (index1 >> 4)) & MASK_BYTE_UNSIGNED);
            if (index * 3 + 1 >= bytes.length) {
                break;
            }

            int index2 = BASE64.indexOf(origString.charAt(i + 2));
            bytes[index * 3 + 1] = (byte) (((index1 << 4) | (index2 >> 2)) & MASK_BYTE_UNSIGNED);
            if (index * 3 + 2 >= bytes.length) {
                break;
            }

            bytes[index * 3 + 2] = (byte) (((index2 << 6) | BASE64.indexOf(origString.charAt(i + 3))) & MASK_BYTE_UNSIGNED);
            index++;
        }

        return bytes;
    }

    /**
     * Convert the given string to HuffmanTree Object using given code mapping
     *
     * @param codeMapping Given code mapping
     * @param content     Given data string
     * @return Converted HuffmanTree object
     */
    public static String encodeWithHuffman(final Hashtable<String, Object> codeMapping, final String content) {
        return HuffmanTree.encodeString(codeMapping, content);
    }

    /**
     * Convert the given string to HuffmanTree Object
     *
     * @param content Given data string
     * @return Converted HuffmanTree object
     */
    public static HuffmanObject encodeWithHuffman(final String content) {
        HuffmanTree huffmanTree = new HuffmanTree();

        String temp = content;
        List<String> checkedStrings = new ArrayList<>();

        while (temp.length() > 0) {
            String keyword = temp.substring(0, 1);
            if (!checkedStrings.contains(keyword)) {
                huffmanTree.insertNode(new HuffmanNode(keyword,
                        StringUtils.countOccurrencesOf(content, keyword)));
                checkedStrings.add(keyword);
            }
            temp = temp.substring(1);
        }

        huffmanTree.build();
        return huffmanTree.encodeString(content);
    }

    /**
     * Check that the given string is MD5 value,
     * because MD5 was deprecated at version 1.1.4, this method will be removed at version 2.0.0
     * @param string    the given string to check
     * @return  <code>true</code> if matched or <code>false</code> not match
     */
    @Deprecated
    public static boolean isMD5(final String string) {
        return StringUtils.notBlank(string) && StringUtils.matches(string.toLowerCase(), RegexGlobals.MD5_VALUE);
    }

    /**
     * Check that the given string is UUID value
     * @param string    the given string to check
     * @return  <code>true</code> if matched or <code>false</code> not match
     */
    public static boolean isUUID(final String string) {
        return StringUtils.notBlank(string) && StringUtils.matches(string.toLowerCase(), RegexGlobals.UUID);
    }

    /**
     * Check that the given string is xml string
     * @param string    the given string to check
     * @return  <code>true</code> if matched or <code>false</code> not match
     */
    public static boolean isXML(final String string) {
        return StringUtils.notBlank(string) && StringUtils.matches(string, RegexGlobals.XML);
    }

    /**
     * Check that the given string is bank card number(Luhn algorithm)
     * @param string    the given string to check
     * @return  <code>true</code> if matched or <code>false</code> not match
     */
    public static boolean isLuhn(final String string) {
        return StringUtils.validateCode(string, CodeType.Luhn);
    }

    /**
     * Check that the given string is China social credit code
     * @param string    the given string to check
     * @return  <code>true</code> if matched or <code>false</code> not match
     */
    public static boolean isChnSocialCredit(final String string) {
        return StringUtils.validateCode(string, CodeType.CHN_Social_Code);
    }

    /**
     * Check that the given string is China ID
     * @param string    the given string to check
     * @return  <code>true</code> if matched or <code>false</code> not match
     */
    public static boolean isChnId(final String string) {
        return StringUtils.validateCode(string, CodeType.CHN_ID_Code);
    }

    /**
     * Check that the given string is phone number support country code start with 00 or +
     * @param string    the given string to check
     * @return  <code>true</code> if matched or <code>false</code> not match
     */
    public static boolean isPhoneNumber(final String string) {
        return StringUtils.notBlank(string) && StringUtils.matches(string, RegexGlobals.PHONE_NUMBER);
    }

    /**
     * Check that the given string is e-mail address
     * @param string    the given string to check
     * @return  <code>true</code> if matched or <code>false</code> not match
     */
    public static boolean isEMail(final String string) {
        return StringUtils.notBlank(string) && StringUtils.matches(string, RegexGlobals.EMAIL_ADDRESS);
    }

    /**
     * Check that the given CharSequence is <code>null</code> or length 0.
     * Note: Will return <code>true</code> for a CharSequence that purely consists of blank.
     * <pre>
     * StringUtils.isEmpty(null) = true
     * StringUtils.isEmpty(Globals.DEFAULT_VALUE_STRING) = true
     * StringUtils.isEmpty(" ") = false
     * StringUtils.isEmpty("Hello") = false
     * </pre>
     *
     * @param str the CharSequence to check (maybe <code>null</code>)
     * @return <code>true</code> if the CharSequence is null or length 0.
     */
    public static boolean isEmpty(final CharSequence str) {
        return !StringUtils.hasLength(str);
    }

    /**
     * Check that the given CharSequence is neither <code>null</code> nor of length 0.
     * Note: Will return <code>true</code> for a CharSequence that purely consists of blank.
     * <pre>
     * StringUtils.notNull(null) = false
     * StringUtils.notNull(Globals.DEFAULT_VALUE_STRING) = false
     * StringUtils.notNull(" ") = true
     * StringUtils.notNull("Hello") = true
     * </pre>
     *
     * @param str the CharSequence to check (maybe <code>null</code>)
     * @return <code>true</code> if the CharSequence is not null and has length
     */
    public static boolean notNull(final CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Check that the given CharSequence is neither <code>null</code> nor only blank character.
     * Note: Will return <code>true</code> for a CharSequence that purely consists of blank.
     * <pre>
     * StringUtils.notBlank(null) = false
     * StringUtils.notBlank(Globals.DEFAULT_VALUE_STRING) = false
     * StringUtils.notBlank(" ") = false
     * StringUtils.notBlank("Hello") = true
     * </pre>
     *
     * @param str the CharSequence to check (maybe <code>null</code>)
     * @return <code>true</code> if the CharSequence is not null/blank character and has length
     */
    public static boolean notBlank(final String str) {
        return (str != null && str.trim().length() > 0);
    }

    /**
     * Check that the given CharSequence is neither <code>null</code> nor of length 0.
     * Note: Will return <code>true</code> for a CharSequence that purely consists of blank.
     * <pre>
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength(Globals.DEFAULT_VALUE_STRING) = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
     * </pre>
     *
     * @param str the CharSequence to check (maybe <code>null</code>)
     * @return <code>true</code> if the CharSequence is not null and has length
     */
    public static boolean hasLength(final CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Check whether the given CharSequence has actual text.
     * More specifically, returns <code>true</code> if the string not <code>null</code>,
     * its length is greater than 0, and it contains at least one non-blank character.
     * <pre>
     * StringUtils.hasText(null) = false
     * StringUtils.hasText(Globals.DEFAULT_VALUE_STRING) = false
     * StringUtils.hasText(" ") = false
     * StringUtils.hasText("12345") = true
     * StringUtils.hasText(" 12345 ") = true
     * </pre>
     *
     * @param str the CharSequence to check (maybe <code>null</code>)
     * @return <code>true</code> if the CharSequence is not <code>null</code>, its length is greater than 0,
     * and it does not contain blank only
     * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespace
     */
    public static boolean hasText(final CharSequence str) {
        if (hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns the length of the string by wrapping it in a byte buffer with
     * the appropriate charset of the input string and returns the limit of the
     * byte buffer
     *
     * @param strIn string
     * @return length of the string
     * @throws ZipException if input string is null. In case of any other exception, this method returns default System charset
     */
    public static int encodedStringLength(final String strIn) {
        return encodedStringLength(strIn, detectCharset(strIn));
    }

    /**
     * returns the length of the string in the input encoding
     *
     * @param str     string
     * @param charset charset encoding
     * @return length of the string
     * @throws ZipException if input string is null. In case of any other exception, this method returns default System charset
     */
    public static int encodedStringLength(final String str, final String charset) {
        if (StringUtils.isEmpty(str)) {
            return Globals.INITIALIZE_INT_VALUE;
        }

        if (StringUtils.isEmpty(charset)) {
            return Globals.DEFAULT_VALUE_INT;
        }

        ByteBuffer byteBuffer;

        try {
            byteBuffer = ByteBuffer.wrap(str.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            byteBuffer = ByteBuffer.wrap(str.getBytes(Charset.defaultCharset()));
        } catch (Exception e) {
            throw new ZipException(e);
        }

        return byteBuffer.limit();
    }

    /**
     * Detects the encoding charset for the input string
     *
     * @param strIn string
     * @return String - charset for the String
     * @throws ZipException if input string is null. In case of any other exception, this method returns default System charset
     */
    public static String detectCharset(final String strIn) {
        if (StringUtils.isEmpty(strIn)) {
            return Globals.DEFAULT_VALUE_STRING;
        }

        try {
            String tempString = new String(strIn.getBytes(Globals.CHARSET_CP850), Globals.CHARSET_CP850);
            if (strIn.equals(tempString)) {
                return Globals.CHARSET_CP850;
            }

            tempString = new String(strIn.getBytes(Globals.CHARSET_GBK), Globals.CHARSET_GBK);
            if (strIn.equals(tempString)) {
                return Globals.CHARSET_GBK;
            }

            tempString = new String(strIn.getBytes(Globals.DEFAULT_ENCODING), Globals.DEFAULT_ENCODING);
            if (strIn.equals(tempString)) {
                return Globals.DEFAULT_ENCODING;
            }
        } catch (Exception e) {
            return Globals.DEFAULT_SYSTEM_CHARSET;
        }
        return Globals.DEFAULT_SYSTEM_CHARSET;
    }

    /**
     * Check whether the given CharSequence contains any blank characters.
     *
     * @param str the CharSequence to check (maybe <code>null</code>)
     * @return <code>true</code> if the CharSequence is not empty and contains at least 1 blank character
     * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespace
     */
    public static boolean containsWhitespace(final CharSequence str) {
        if (hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the given String contains any blank characters.
     *
     * @param str the String to check (maybe <code>null</code>)
     * @return <code>true</code> if the String is not empty and contains at least 1 blank character
     * @see #containsWhitespace(CharSequence) #containsWhitespace(CharSequence)
     */
    public static boolean containsWhitespace(final String str) {
        return containsWhitespace((CharSequence) str);
    }

    /**
     * Trim leading and trailing blank from the given String.
     *
     * @param str the String to check
     * @return the trimmed String
     * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespace
     */
    public static String trimWhitespace(final String str) {
        String string = StringUtils.trimLeadingWhitespace(str);
        string = StringUtils.trimTrailingWhitespace(string);
        return string;
    }

    /**
     * Trim <i>all</i> blank from the given String:
     * leading, trailing, and in between characters.
     *
     * @param str the String to check
     * @return the trimmed String
     * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespace
     */
    public static String trimAllWhitespace(final String str) {
        if (hasLength(str)) {
            return str;
        }
        StringBuilder buf = new StringBuilder(str);
        int index = 0;
        while (buf.length() > index) {
            if (Character.isWhitespace(buf.charAt(index))) {
                buf.deleteCharAt(index);
            } else {
                index++;
            }
        }
        return buf.toString();
    }

    /**
     * Trim leading blank from the given String.
     *
     * @param str the String to check
     * @return the trimmed String
     * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespace
     */
    public static String trimLeadingWhitespace(final String str) {
        if (hasLength(str)) {
            return str;
        }
        StringBuilder buf = new StringBuilder(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
            buf.deleteCharAt(0);
        }
        return buf.toString();
    }

    /**
     * Trim trailing blank from the given String.
     *
     * @param str the String to check
     * @return the trimmed String
     * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespace
     */
    public static String trimTrailingWhitespace(final String str) {
        if (hasLength(str)) {
            return str;
        }
        StringBuilder buf = new StringBuilder(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

    /**
     * Trim all occurrences of the supplied leading character from the given String.
     *
     * @param str              the String to check
     * @param leadingCharacter the leading character to be trimmed
     * @return the trimmed String
     */
    public static String trimLeadingCharacter(final String str, final char leadingCharacter) {
        if (hasLength(str)) {
            return str;
        }
        StringBuilder buf = new StringBuilder(str);
        while (buf.length() > 0 && buf.charAt(0) == leadingCharacter) {
            buf.deleteCharAt(0);
        }
        return buf.toString();
    }

    /**
     * Trim all occurrences of the supplied trailing character from the given String.
     *
     * @param str               the String to check
     * @param trailingCharacter the trailing character to be trimmed
     * @return the trimmed String
     */
    public static String trimTrailingCharacter(final String str, final char trailingCharacter) {
        if (hasLength(str)) {
            return str;
        }
        StringBuilder buf = new StringBuilder(str);
        while (buf.length() > 0 && buf.charAt(buf.length() - 1) == trailingCharacter) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }


    /**
     * Test if the given String starts with the specified prefix,
     * ignoring the upper/lower case.
     *
     * @param str    the String to check
     * @param prefix the prefix to look for
     * @return check result
     * @see java.lang.String#startsWith java.lang.String#startsWith
     */
    public static boolean startsWithIgnoreCase(final String str, final String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.startsWith(prefix)) {
            return true;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        String lcStr = str.substring(0, prefix.length()).toLowerCase();
        String lcPrefix = prefix.toLowerCase();
        return lcStr.equals(lcPrefix);
    }

    /**
     * Test if the given String ends with the specified suffix,
     * ignoring the upper/lower case.
     *
     * @param str    the String to check
     * @param suffix the suffix to look for
     * @return check result
     * @see java.lang.String#endsWith java.lang.String#endsWith
     */
    public static boolean endsWithIgnoreCase(final String str, final String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        if (str.endsWith(suffix)) {
            return true;
        }
        if (str.length() < suffix.length()) {
            return false;
        }

        String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
        String lcSuffix = suffix.toLowerCase();
        return lcStr.equals(lcSuffix);
    }

    /**
     * Check given string contains emoji info
     *
     * @param string Given string
     * @return Check result
     */
    public static boolean containsEmoji(final String string) {
        if (string != null && string.length() > 0) {
            int length = string.length();
            for (int i = 0; i < length; i++) {
                char c = string.charAt(i);
                if (0xd800 <= c && c <= 0xdbff) {
                    if (length > 1) {
                        char next = string.charAt(i + 1);
                        int result = ((c - 0xd800) * 0x400) + (next - 0xdc00) + 0x10000;
                        if (0x1d000 <= result && result <= 0x1f77f) {
                            return true;
                        }
                    }
                } else {
                    if ((0x2100 <= c && c <= 0x27ff && c != 0x263b)
                            || (0x2805 <= c && c <= 0x2b07)
                            || (0x3297 <= c && c <= 0x3299)
                            || c == 0xa9 || c == 0xae || c == 0x303d
                            || c == 0x3030 || c == 0x2b55 || c == 0x2b1c
                            || c == 0x2b1b || c == 0x2b50) {
                        return true;
                    }

                    if (length > 1 && i < (length - 1)) {
                        char next = string.charAt(i + 1);
                        if (next == 0x20e3) {
                            return true;
                        }
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Test whether the given string matches the given substring
     * at the given index.
     *
     * @param str       the original string (or StringBuilder)
     * @param index     the index in the original string to start matching against
     * @param substring the substring to match at the given index
     * @return check result
     */
    public static boolean substringMatch(final CharSequence str, final int index, final CharSequence substring) {
        for (int j = 0; j < substring.length(); j++) {
            int i = index + j;
            if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Count the occurrences of the substring in string s.
     *
     * @param str string to search in. Return 0 if this is null.
     * @param sub string to search for. Return 0 if this is null.
     * @return count result
     */
    public static int countOccurrencesOf(final String str, final String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0, pos = 0, idx;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }

    /**
     * Replace all occurrences of a substring within a string with
     * another string.
     *
     * @param inString   String to examine
     * @param oldPattern String to replace
     * @param newPattern String to insert
     * @return a String with the replacements
     */
    public static String replace(final String inString, final String oldPattern, final String newPattern) {
        if (inString == null || oldPattern == null || newPattern == null) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        // output StringBuilder we'll build up
        int pos = 0; // our position in the old string
        int index = inString.indexOf(oldPattern);
        // the index of an occurrence we've found, or -1
        int patLen = oldPattern.length();
        while (index >= 0) {
            stringBuilder.append(inString, pos, index);
            stringBuilder.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        stringBuilder.append(inString.substring(pos));

        // remember to append any characters to the right of a match
        return stringBuilder.toString();
    }

    /**
     * Delete all occurrences of the given substring.
     *
     * @param inString the original String
     * @param pattern  the pattern to delete all occurrences of
     * @return the resulting String
     */
    public static String delete(final String inString, final String pattern) {
        return replace(inString, pattern, Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Delete any character in a given String.
     *
     * @param inString      the original String
     * @param charsToDelete a set of characters to delete. E.g. "az\n" will delete 'a's, 'z's and new lines.
     * @return the resulting String
     */
    public static String deleteAny(final String inString, final String charsToDelete) {
        if (hasLength(inString) || hasLength(charsToDelete)) {
            return inString;
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * Quote the given String with single quotes.
     *
     * @param str the input String (e.g. "myString")
     * @return the quoted String (e.g. "'myString'"), or <code>null</code> if the input was <code>null</code>
     */
    public static String quote(final String str) {
        return (str != null ? "'" + str + "'" : null);
    }

    /**
     * Turn the given Object into a String with single quotes
     * if it is a String; keeping the Object as-is else.
     *
     * @param obj the input Object (e.g. "myString")
     * @return the quoted String (e.g. "'myString'"), or the input object as-is if not a String
     */
    public static Object quoteIfString(final Object obj) {
        return (obj instanceof String ? quote((String) obj) : obj);
    }

    /**
     * Unqualified a string qualified by a '.' dot character. For example,
     * "this.name.is.qualified", returns "qualified".
     *
     * @param qualifiedName the qualified name
     * @return qualified string
     */
    public static String unqualified(final String qualifiedName) {
        return unqualified(qualifiedName, '.');
    }

    /**
     * Unqualified a string qualified by a separator character. For example,
     * "this:name:is:qualified" returns "qualified" if using a ':' separator.
     *
     * @param qualifiedName the qualified name
     * @param separator     the separator
     * @return qualified string
     */
    public static String unqualified(final String qualifiedName, final char separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }

    /**
     * Capitalize a <code>String</code>, changing the first letter to
     * the upper case as per {@link Character#toUpperCase(char)}.
     * No other letters are changed.
     *
     * @param str the String to capitalize, maybe <code>null</code>
     * @return the capitalized String, <code>null</code> if null
     */
    public static String capitalize(final String str) {
        return changeFirstCharacterCase(str, true);
    }

    /**
     * Uncapitalized a <code>String</code>, changing the first letter to
     * lower case as per {@link Character#toLowerCase(char)}.
     * No other letters are changed.
     *
     * @param str the String to uncapitalized, maybe <code>null</code>
     * @return the uncapitalized String, <code>null</code> if null
     */
    public static String uncapitalized(final String str) {
        return changeFirstCharacterCase(str, false);
    }

    /**
     * Extract the filename from the given path,
     * e.g. "mypath/myfile.txt" -&gt; "myfile.txt".
     *
     * @param path the file path (maybe <code>null</code>)
     * @return the extracted filename, or <code>null</code> if none
     */
    public static String getFilename(final String path) {
        if (path == null) {
            return null;
        }
        String cleanPath = cleanPath(path);
        int separatorIndex = cleanPath.lastIndexOf(Globals.DEFAULT_PAGE_SEPARATOR);
        return (separatorIndex != -1 ? cleanPath.substring(separatorIndex + 1) : cleanPath);
    }

    /**
     * Extract the filename extension from the given path,
     * e.g. "mypath/myfile.txt" -&gt; "txt".
     *
     * @param path the file path (maybe <code>null</code>)
     * @return the extracted filename extension, or <code>null</code> if none
     */
    public static String getFilenameExtension(final String path) {
        if (path == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        int sepIndex = path.lastIndexOf(Globals.EXTENSION_SEPARATOR);
        return (sepIndex != -1 ? path.substring(sepIndex + 1) : Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Strip the filename extension from the given path,
     * e.g. "mypath/myfile.txt" -&gt; "mypath/myfile".
     *
     * @param path the file path (maybe <code>null</code>)
     * @return the path with stripped filename extension, or <code>null</code> if none
     */
    public static String stripFilenameExtension(final String path) {
        if (path == null) {
            return null;
        }
        int sepIndex = path.lastIndexOf(Globals.EXTENSION_SEPARATOR);
        return (sepIndex != -1 ? path.substring(0, sepIndex) : path);
    }

    /**
     * Apply the given relative path to the given path,
     * assuming standard Java folder separation (i.e. "/" separators);
     *
     * @param path         the path to start from (usually a full file path)
     * @param relativePath the relative path to apply (relative to the full file path above)
     * @return the full file path that results from applying the relative path
     */
    public static String applyRelativePath(final String path, final String relativePath) {
        int separatorIndex = path.lastIndexOf(Globals.DEFAULT_PAGE_SEPARATOR);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(Globals.DEFAULT_PAGE_SEPARATOR)) {
                newPath += Globals.DEFAULT_PAGE_SEPARATOR;
            }
            return newPath + relativePath;
        } else {
            return relativePath;
        }
    }

    /**
     * Normalize the path by suppressing sequences like "path/.." and
     * inner simple dots.
     * <p>The result is convenient for path comparison. For other uses,
     * notice that Windows separators ("\") are replaced by simple slashes.
     *
     * @param path the original path
     * @return the normalized path
     */
    public static String cleanPath(final String path) {
        String pathToUse = path;

        // Strip prefix from path to analyze, to not treat it as part of the
        // first path element. This is necessary to correctly parse paths like
        // "file:core/../core/io/Resource.class", where the ".." Should just
        // strip the first "core" directory while keeping the "file:" prefix.
        int prefixIndex = pathToUse.indexOf(":");
        String prefix = Globals.DEFAULT_VALUE_STRING;
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            pathToUse = pathToUse.substring(prefixIndex + 1);
        }

        String[] pathArray = delimitedListToStringArray(pathToUse, Globals.DEFAULT_PAGE_SEPARATOR);
        List<String> pathElements = new LinkedList<>();
        int tops = 0;

        for (int i = pathArray.length - 1; i >= 0; i--) {
            if (!CURRENT_PATH.equals(pathArray[i])) {
                if (TOP_PATH.equals(pathArray[i])) {
                    // Registering the top path found.
                    tops++;
                } else {
                    if (tops > 0) {
                        // Merging the path element with corresponding to the top path.
                        tops--;
                    } else {
                        // Normal path element found.
                        pathElements.add(0, pathArray[i]);
                    }
                }
            }
        }

        // Remaining top paths need to be retained.
        for (int i = 0; i < tops; i++) {
            pathElements.add(0, TOP_PATH);
        }

        return prefix + collectionToDelimitedString(pathElements, Globals.DEFAULT_PAGE_SEPARATOR);
    }

    /**
     * Compare two paths after normalization of them.
     *
     * @param path1 first path for comparison
     * @param path2 second path for comparison
     * @return whether the two paths are equivalent after normalization
     */
    public static boolean pathEquals(final String path1, final String path2) {
        return cleanPath(path1).equals(cleanPath(path2));
    }

    /**
     * Parse the given <code>localeString</code> into a {@link Locale}.
     * <p>This is the inverse operation of {@link Locale#toString Locale's toString}.
     *
     * @param localeString the locale string, following <code>Locale's</code> <code>toString()</code> format ("en", "en_UK", etc);
     *                     also accepts spaces as separators, as an alternative to underscore
     * @return a corresponding <code>Locale</code> instance
     */
    public static Locale parseLocaleString(final String localeString) {
        if (localeString == null) {
            return null;
        }
        String[] parts = tokenizeToStringArray(localeString, "_", false, false);

        if (parts == null) {
            return null;
        }

        String language = (parts.length > 0 ? parts[0] : Globals.DEFAULT_VALUE_STRING);
        String country = (parts.length > 1 ? parts[1] : Globals.DEFAULT_VALUE_STRING);
        String variant = Globals.DEFAULT_VALUE_STRING;
        if (parts.length >= 2) {
            // There is definitely a variant, and it is everything after the country
            // code sans the separator between the country code and the variant.
            int endIndexOfCountryCode = localeString.indexOf(country) + country.length();
            // Strip off any leading '_' and blank, what's left is the variant.
            variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
            if (variant.startsWith("_")) {
                variant = trimLeadingCharacter(variant, '_');
            }
        }
        return (language.length() > 0 ? new Locale(language, country, variant) : null);
    }

    //---------------------------------------------------------------------
    // Convenience methods for working with String arrays
    //---------------------------------------------------------------------

    /**
     * Append the given String to the given String array, returning a new array
     * consisting of the input array contents plus the given String.
     *
     * @param array the array to append to (can be <code>null</code>)
     * @param str   the String to append
     * @return the new array (never <code>null</code>)
     */
    public static String[] addStringToArray(final String[] array, final String str) {
        if (ObjectUtils.isEmpty(array)) {
            return new String[]{str};
        }
        String[] newArr = new String[array.length + 1];
        System.arraycopy(array, 0, newArr, 0, array.length);
        newArr[array.length] = str;
        return newArr;
    }

    /**
     * Concatenate the given String arrays into one,
     * with overlapping array elements included twice.
     * <p>The order of elements in the original arrays is preserved.
     *
     * @param array1 the first array (can be <code>null</code>)
     * @param array2 the second array (can be <code>null</code>)
     * @return the new array (<code>null</code> if both given arrays were <code>null</code>)
     */
    public static String[] concatenateStringArrays(final String[] array1, final String[] array2) {
        if (ObjectUtils.isEmpty(array1)) {
            return array2;
        }
        if (ObjectUtils.isEmpty(array2)) {
            return array1;
        }
        String[] newArr = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, newArr, 0, array1.length);
        System.arraycopy(array2, 0, newArr, array1.length, array2.length);
        return newArr;
    }

    /**
     * Merge the given String arrays into one, with overlapping
     * array elements only included once.
     * <p>The order of elements in the original arrays is preserved
     * (except for overlapping elements, which are only
     * included on their first occurrence).
     *
     * @param array1 the first array (can be <code>null</code>)
     * @param array2 the second array (can be <code>null</code>)
     * @return the new array (<code>null</code> if both given arrays were <code>null</code>)
     */
    public static String[] mergeStringArrays(final String[] array1, final String[] array2) {
        if (ObjectUtils.isEmpty(array1)) {
            return array2;
        }
        if (ObjectUtils.isEmpty(array2)) {
            return array1;
        }
        List<String> result = new ArrayList<>(Arrays.asList(array1));
        for (String str : array2) {
            if (!result.contains(str)) {
                result.add(str);
            }
        }
        return toStringArray(result);
    }

    /**
     * Turn given sources String arrays into sorted arrays.
     *
     * @param array the source array
     * @return the sorted array (never <code>null</code>)
     */
    public static String[] sortStringArray(final String[] array) {
        if (ObjectUtils.isEmpty(array)) {
            return new String[0];
        }
        Arrays.sort(array);
        return array;
    }

    /**
     * Copy the given Collection into a String array.
     * The Collection must contain String elements only.
     *
     * @param collection the Collection to copy
     * @return the String array (<code>null</code> if the passed-in Collection was <code>null</code>)
     */
    public static String[] toStringArray(final Collection<String> collection) {
        if (collection == null) {
            return new String[0];
        }
        return collection.toArray(new String[0]);
    }

    /**
     * Copy the given Enumeration into a String array.
     * The Enumeration must contain String elements only.
     *
     * @param enumeration the Enumeration to copy
     * @return the String array (<code>null</code> if the passed-in Enumeration was <code>null</code>)
     */
    public static String[] toStringArray(final Enumeration<String> enumeration) {
        if (enumeration == null) {
            return new String[0];
        }
        List<String> list = Collections.list(enumeration);
        return list.toArray(new String[0]);
    }

    /**
     * Trim the elements of the given String array,
     * calling <code>String.trim()</code> on each of them.
     *
     * @param array the original String array
     * @return the resulting array (of the same size) with trimmed elements
     */
    public static String[] trimArrayElements(final String[] array) {
        if (ObjectUtils.isEmpty(array)) {
            return new String[0];
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            String element = array[i];
            result[i] = (element != null ? element.trim() : null);
        }
        return result;
    }

    /**
     * Remove duplicate Strings from the given array.
     * Also sorts the array, as it uses a TreeSet.
     *
     * @param array the String array
     * @return an array without duplicates, in natural sort order
     */
    public static String[] removeDuplicateStrings(final String[] array) {
        if (ObjectUtils.isEmpty(array)) {
            return array;
        }
        Set<String> set = new TreeSet<>();
        Collections.addAll(set, array);
        return toStringArray(set);
    }

    /**
     * Split a String at the first occurrence of the delimiter.
     * Does not include the delimiter in the result.
     *
     * @param toSplit   the string to split
     * @param delimiter to split the string up with
     * @return a two element array with index 0 being before the delimiter, and index 1 being after the delimiter (neither element includes the delimiter); or <code>null</code> if the delimiter wasn't found in the given input String
     */
    public static String[] split(final String toSplit, final String delimiter) {
        if (hasLength(toSplit) || hasLength(delimiter)) {
            return null;
        }
        int offset = toSplit.indexOf(delimiter);
        if (offset < 0) {
            return new String[]{toSplit};
        }
        String beforeDelimiter = toSplit.substring(0, offset);
        String afterDelimiter = toSplit.substring(offset + delimiter.length());
        return new String[]{beforeDelimiter, afterDelimiter};
    }

    /**
     * Take an array Strings and split each element based on the given delimiter.
     * A <code>Properties</code> instance is then generated, with the left of the
     * delimiter providing the key, and the right of the delimiter providing the value.
     * <p>Will trim both the key and value before adding them to the
     * <code>Properties</code> instance.
     *
     * @param array     the array to process
     * @param delimiter to split each element using (typically the equals symbol)
     * @return a <code>Properties</code> instance representing the array contents, or <code>null</code> if the array to process was null or empty
     */
    public static Properties splitArrayElementsIntoProperties(final String[] array, final String delimiter) {
        return splitArrayElementsIntoProperties(array, delimiter, null);
    }

    /**
     * Take an array Strings and split each element based on the given delimiter.
     * A <code>Properties</code> instance is then generated, with the left of the
     * delimiter providing the key, and the right of the delimiter providing the value.
     * <p>Will trim both the key and value before adding them to the
     * <code>Properties</code> instance.
     *
     * @param array         the array to process
     * @param delimiter     to split each element using (typically the equals symbol)
     * @param charsToDelete one or more characters to remove from each element prior to attempting the split operation (typically the quotation mark symbol), or <code>null</code> if no removal should occur
     * @return a <code>Properties</code> instance representing the array contents, or <code>null</code> if the array to process was <code>null</code> or empty
     */
    public static Properties splitArrayElementsIntoProperties(final String[] array, final String delimiter,
                                                              final String charsToDelete) {

        if (ObjectUtils.isEmpty(array)) {
            return null;
        }
        Properties result = new Properties();
        for (String string : array) {
            String element = string;
            if (charsToDelete != null) {
                element = deleteAny(string, charsToDelete);
            }
            String[] splitterElement = split(element, delimiter);
            if (splitterElement == null) {
                continue;
            }
            result.setProperty(splitterElement[0].trim(), splitterElement[1].trim());
        }
        return result;
    }

    /**
     * Tokenize the given String into a String array via a StringTokenizer.
     * Trims tokens and omits empty tokens.
     * <p>The given delimiters string is supposed to consist of any number of
     * delimiter characters. Each of those characters can be used to separate
     * tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using <code>delimitedListToStringArray</code>
     *
     * @param str        the String to tokenize
     * @param delimiters the delimiter characters, assembled as String (each of those characters is individually considered as delimiter).
     * @return an array of the tokens
     * @see java.util.StringTokenizer
     * @see java.lang.String#trim() java.lang.String#trim()
     * @see #delimitedListToStringArray #delimitedListToStringArray
     */
    public static String[] tokenizeToStringArray(final String str, final String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    /**
     * Tokenize the given String into a String array via a StringTokenizer.
     * <p>The given delimiters string is supposed to consist of any number of
     * delimiter characters. Each of those characters can be used to separate
     * tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using <code>delimitedListToStringArray</code>
     *
     * @param str               the String to tokenize
     * @param delimiters        the delimiter characters, assembled as String (each of those characters is individually considered as delimiter)
     * @param trimTokens        trim the tokens via String's <code>trim</code>
     * @param ignoreEmptyTokens omit empty tokens from the result array (only applies to tokens that are empty after trimming; StringTokenizer will not consider subsequent delimiters as token in the first place).
     * @return an array of the tokens (<code>null</code> if the input String was <code>null</code>)
     * @see java.util.StringTokenizer
     * @see java.lang.String#trim() java.lang.String#trim()
     * @see #delimitedListToStringArray #delimitedListToStringArray
     */
    public static String[] tokenizeToStringArray(final String str, final String delimiters, final boolean trimTokens,
                                                 final boolean ignoreEmptyTokens) {

        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    /**
     * Take a String which is a delimited list and convert it to a String array.
     * <p>A single delimiter can consist of more than one character: It will still
     * be considered as single delimiter string, rather than as a bunch of potential
     * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
     *
     * @param str       the input String
     * @param delimiter the delimiter between elements (this is a single delimiter, rather than a bunch individual delimiter characters)
     * @return an array of the tokens in the list
     */
    public static String[] delimitedListToStringArray(final String str, final String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }

    /**
     * Take a String which is a delimited list and convert it to a String array.
     * <p>A single delimiter can consist of more than one character: It will still
     * be considered as single delimiter string, rather than as a bunch of potential
     * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
     *
     * @param str           the input String
     * @param delimiter     the delimiter between elements (this is a single delimiter, rather than a bunch individual delimiter characters)
     * @param charsToDelete a set of characters to delete.
     *                      Useful for deleting unwanted line breaks: e.g. "\r\n\f" will delete all new lines, line feeds in a String.
     * @return an array of the tokens in the list
     */
    public static String[] delimitedListToStringArray(final String str, final String delimiter,
                                                      final String charsToDelete) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[]{str};
        }
        List<String> result = new ArrayList<>();
        if (Globals.DEFAULT_VALUE_STRING.equals(delimiter)) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int pos = 0;
            int delPos;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                // Add rest of String, but not in case of empty input.
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }

    /**
     * Convert a CSV list into an array of Strings.
     *
     * @param str the input String
     * @return an array of Strings, or the empty array in case of empty input
     */
    public static String[] commaDelimitedListToStringArray(final String str) {
        return delimitedListToStringArray(str, ",");
    }

    /**
     * Convenience method to convert a CSV string list to a set.
     * Note that this will suppress duplicates.
     *
     * @param str the input String
     * @return a Set of String entries in the list
     */
    public static Set<String> commaDelimitedListToSet(final String str) {
        Set<String> set = new TreeSet<>();
        String[] tokens = commaDelimitedListToStringArray(str);
        Collections.addAll(set, tokens);
        return set;
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for <code>toString()</code> implementations.
     *
     * @param coll      the Collection to display
     * @param delimiter the delimiter to use (probably a ",")
     * @param prefix    the String to start each element with
     * @param suffix    the String to end each element with
     * @return the delimited String
     */
    public static String collectionToDelimitedString(final Collection<String> coll, final String delimiter,
                                                     final String prefix, final String suffix) {
        if (CollectionUtils.isEmpty(coll)) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for <code>toString()</code> implementations.
     *
     * @param coll      the Collection to display
     * @param delimiter the delimiter to use (probably a ",")
     * @return the delimited String
     */
    public static String collectionToDelimitedString(final Collection<String> coll, final String delimiter) {
        return collectionToDelimitedString(coll, delimiter, Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Convenience method to return a Collection as a CSV String.
     * E.g., useful for <code>toString()</code> implementations.
     *
     * @param coll the Collection to display
     * @return the delimited String
     */
    public static String collectionToCommaDelimitedString(final Collection<String> coll) {
        return collectionToDelimitedString(coll, ",");
    }

    /**
     * Contains ignore case boolean.
     *
     * @param string the string
     * @param search the search
     * @return the boolean
     */
    public static boolean containsIgnoreCase(final String string, final String search) {
        if (string == null || search == null) {
            return false;
        }

        int length = search.length();
        int maxLength = string.length() - length;

        for (int i = 0; i < maxLength; i++) {
            if (string.regionMatches(true, i, search, 0, length)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience method to return a String array as a delimited (e.g. CSV)
     * String. E.g. useful for <code>toString()</code> implementations.
     *
     * @param arr       the array to display
     * @param delimiter the delimiter to use (probably a ",")
     * @return the delimited String
     */
    public static String arrayToDelimitedString(final Object[] arr, final String delimiter) {
        if (ObjectUtils.isEmpty(arr)) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    /**
     * Convenience method to return a String array as a CSV String.
     * E.g., useful for <code>toString()</code> implementations.
     *
     * @param arr the array to display
     * @return the delimited String
     */
    public static String arrayToCommaDelimitedString(final Object[] arr) {
        return arrayToDelimitedString(arr, ",");
    }

    /**
     * Convert BLOB to string and format for HTML
     *
     * @param content BLOB datas
     * @return Convert string
     */
    public static String convertContent(final byte[] content) {
        if (content == null) {
            return null;
        }
        return textToHtml(ConvertUtils.convertToString(content));
    }

    /**
     * The enum String type.
     */
    public enum StringType {
        /**
         * Json string type.
         */
        JSON,
        /**
         * Yaml string type.
         */
        YAML,
        /**
         * XML string type
         */
        XML,
        /**
         * Simple string type, include basic type wrapper class, etc.
         */
        SIMPLE
    }

    /**
     * Object to string.
     *
     * @param object       the object
     * @param stringType   the string type
     * @param formatOutput the format output
     * @return the string
     */
    public static String objectToString(final Object object, final StringType stringType, final boolean formatOutput) {
        ObjectMapper objectMapper;
        switch (stringType) {
            case JSON -> objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            case YAML ->
                    objectMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
                            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            default -> {
                return Globals.DEFAULT_VALUE_STRING;
            }
        }
        try {
            return formatOutput
                    ? objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object)
                    : objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            if (StringUtils.LOGGER.isDebugEnabled()) {
                StringUtils.LOGGER.debug("Convert object to string error! ", e);
            }
        }
        return Globals.DEFAULT_VALUE_STRING;
    }

    /**
     * Parse xml string to target bean class
     *
     * @param <T>        Template
     * @param string     Parsed string
     * @param beanClass  Target bean class
     * @param schemaPaths the schema path
     * @return Converted object
     */
    public static <T> T stringToObject(final String string, final Class<T> beanClass, final String... schemaPaths) {
        return stringToObject(string, Globals.DEFAULT_ENCODING, beanClass, schemaPaths);
    }

    /**
     * Parse string to target bean class
     *
     * @param <T>       Template
     * @param string    Parsed string
     * @param beanClass Target bean class
     * @return Converted object
     */
    public static <T> T stringToObject(final String string, final String encoding,
                                       final Class<T> beanClass, final String... schemaPaths) {
        if (StringUtils.isEmpty(string)) {
            LOGGER.error("Can't parse empty string");
            return null;
        }
        if (string.startsWith("<")) {
            return stringToObject(string, StringType.XML, encoding, beanClass, schemaPaths);
        }
        if (string.startsWith("{")) {
            return stringToObject(string, StringType.JSON, encoding, beanClass, schemaPaths);
        }
        return stringToObject(string, StringType.YAML, encoding, beanClass, schemaPaths);
    }

    /**
     * Parse string to target bean class
     *
     * @param <T>       Template
     * @param string    Parsed string
     * @param encoding  String encoding, just using for parse xml
     * @param beanClass Target bean class
     * @return Converted object
     */
    public static <T> List<T> stringToList(final String string, final String encoding, final Class<T> beanClass) {
        if (StringUtils.isEmpty(string)) {
            LOGGER.error("Can't parse empty string");
            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parse string: {} use encoding: {} to bean: {}", string, encoding, beanClass.getName());
        }

        String stringEncoding = (encoding == null) ? Globals.DEFAULT_ENCODING : encoding;
        try (InputStream inputStream = new ByteArrayInputStream(string.getBytes(stringEncoding))) {
            return streamToList(inputStream, beanClass);
        } catch (IOException e) {
            LOGGER.error("Parse string error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
            return new ArrayList<>();
        }
    }

    /**
     * Parse file content to target bean class
     *
     * @param <T>         Template
     * @param filePath    File path
     * @param beanClass   Target bean class
     * @param schemaPaths Schema file paths
     * @return Converted object
     */
    public static <T> T fileToObject(final String filePath, final Class<T> beanClass, final String... schemaPaths) {
        if (StringUtils.isEmpty(filePath) || !FileUtils.isExists(filePath)) {
            LOGGER.error("Can't found file: {}", filePath);
            return null;
        }
        String extName = StringUtils.getFilenameExtension(filePath);
        try (InputStream inputStream = FileUtils.loadFile(filePath)) {
            return switch (extName.toLowerCase()) {
                case "json" -> streamToObject(inputStream, StringType.JSON, beanClass, Globals.DEFAULT_VALUE_STRING);
                case "xml" -> streamToObject(inputStream, StringType.XML, beanClass, schemaPaths);
                case "yml", "yaml" ->
                        streamToObject(inputStream, StringType.YAML, beanClass, Globals.DEFAULT_VALUE_STRING);
                default -> null;
            };
        } catch (IOException e) {
            LOGGER.error("Parse file error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
        }
        return null;
    }

    /**
     * Parse stream t.
     *
     * @param <T>         the type parameter
     * @param inputStream the input stream
     * @param stringType  the string type
     * @param beanClass   the bean class
     * @param schemaPath  Schema file path
     * @return the t
     * @throws IOException the io exception
     */
    public static <T> T streamToObject(final InputStream inputStream, final StringType stringType,
                                       final Class<T> beanClass, final String... schemaPath) throws IOException {
        if (StringType.XML.equals(stringType)) {
            try {
                Unmarshaller unmarshaller = JAXBContext.newInstance(beanClass).createUnmarshaller();
                if (schemaPath.length > 0) {
                    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Schema schema = null;
                    if (schemaPath.length == 1) {
                        schema = schemaFactory.newSchema(FileUtils.getFile(SCHEMA_MAPPING.getOrDefault(schemaPath[0], schemaPath[0])));
                    } else {
                        schemaFactory.setResourceResolver(new SchemaResourceResolver());
                        try {
                            Source[] sources = new Source[schemaPath.length];
                            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                            docFactory.setNamespaceAware(Boolean.TRUE);
                            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                            for (int i = 0; i < schemaPath.length; i++) {
                                String locationPath = SCHEMA_MAPPING.getOrDefault(schemaPath[i], schemaPath[i]);
                                Document document = docBuilder.parse(FileUtils.getFile(locationPath));
                                sources[i] = new DOMSource(document, locationPath);
                            }
                            schema = schemaFactory.newSchema(sources);
                        } catch (ParserConfigurationException e) {
                            LOGGER.warn("Load schema error, validation ignored! ");
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Parse schemas error! ", e);
                            }
                        }
                    }
                    unmarshaller.setSchema(schema);
                }
                return beanClass.cast(unmarshaller.unmarshal(inputStream));
            } catch (JAXBException | SAXException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parse error! ", e);
                }
                return null;
            }
        } else {
            ObjectMapper objectMapper;
            switch (stringType) {
                case JSON -> objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                case YAML ->
                        objectMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
                                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                default -> {
                    return null;
                }
            }
            return objectMapper.readValue(inputStream, beanClass);
        }
    }

    /**
     * Parse string to target bean class
     *
     * @param <T>         Template
     * @param inputStream the input stream
     * @param beanClass   Target bean class
     * @return Converted object
     */
    public static <T> List<T> streamToList(final InputStream inputStream, final Class<T> beanClass) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, beanClass);
        return objectMapper.readValue(inputStream, javaType);
    }

    /**
     * Convert JSON string to map
     *
     * @param data       the data
     * @param stringType the string type
     * @return Convert map
     */
    public static Map<String, Object> dataToMap(final String data, final StringType stringType) {
        ObjectMapper objectMapper;
        switch (stringType) {
            case JSON -> objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            case YAML ->
                    objectMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
                            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            default -> {
                return new HashMap<>();
            }
        }
        try {
            return objectMapper.readValue(data, new TypeReference<>() {
            });
        } catch (Exception e) {
            if (StringUtils.LOGGER.isDebugEnabled()) {
                StringUtils.LOGGER.debug("Convert json string to object bean error! ", e);
            }
        }

        return new HashMap<>();
    }

    /**
     * Replace special XMl character with converted character in string
     *
     * @param sourceString input string
     * @return replaced string
     */
    public static String formatTextForXML(final String sourceString) {
        if (sourceString == null) {
            return null;
        }
        int strLen;
        StringBuilder reString = new StringBuilder();
        String deString;
        strLen = sourceString.length();

        for (int i = 0; i < strLen; i++) {
            char ch = sourceString.charAt(i);
            deString = switch (ch) {
                case '<' -> "&lt;";
                case '>' -> "&gt;";
                case '\"' -> "&quot;";
                case '&' -> "&amp;";
                case 13 -> Globals.DEFAULT_VALUE_STRING;
                default -> Globals.DEFAULT_VALUE_STRING + ch;
            };
            reString.append(deString);
        }
        return reString.toString();
    }

    /**
     * Replace converted character with special XMl character in string
     *
     * @param sourceString input string
     * @return replaced string
     */
    public static String formatForText(final String sourceString) {

        if (StringUtils.isEmpty(sourceString)) {
            return sourceString;
        }

        String replaceString = replace(sourceString, "&amp;", "&");
        replaceString = replace(replaceString, "&lt;", "<");
        replaceString = replace(replaceString, "&gt;", ">");
        replaceString = replace(replaceString, "&quot;", "\"");
        replaceString = replace(replaceString, "&#39;", "'");
        replaceString = replace(replaceString, "\\\\", "\\");
        replaceString = replace(replaceString, "\\n", Character.toString(FileUtils.LF));
        replaceString = replace(replaceString, "\\r", Character.toString(FileUtils.CR));
        replaceString = replace(replaceString, "<br/>", Character.toString(FileUtils.CR));

        return replaceString;
    }

    /**
     * Replace special HTML character with converted character in string
     *
     * @param sourceString input string
     * @return replaced string
     */
    public static String textToHtml(final String sourceString) {
        int strLen;
        StringBuilder reString = new StringBuilder();
        strLen = sourceString.length();

        for (int i = 0; i < strLen; i++) {
            char ch = sourceString.charAt(i);
            switch (ch) {
                case '<' -> reString.append("&lt;");
                case '>' -> reString.append("&gt;");
                case '\"' -> reString.append("&quot;");
                case '&' -> reString.append("&amp;");
                case '\'' -> reString.append("&#39;");
                case '\\' -> reString.append("\\\\");
                case FileUtils.LF -> reString.append("\\n");
                case FileUtils.CR -> reString.append("<br/>");
                default -> reString.append(Globals.DEFAULT_VALUE_STRING).append(ch);
            }
        }
        return reString.toString();
    }

    /**
     * Matches with regex
     *
     * @param str   input string
     * @param regex regex message
     * @return match result
     */
    public static boolean matches(final String str, final String regex) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(regex)) {
            return Boolean.FALSE;
        }
        return str.matches(regex);
    }

    /**
     * Replace template string with regex
     *
     * @param str      input string
     * @param regex    regex message
     * @param template template string
     * @return replaced string. null for match failed
     */
    public static String replaceWithRegex(final String str, final String regex, final String template) {
        return replaceWithRegex(str, regex, template, Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Replace template string with regex
     *
     * @param str             input string
     * @param regex           regex message
     * @param template        template string
     * @param substringPrefix the substring prefix
     * @return replaced string. null for match failed
     */
    public static String replaceWithRegex(final String str, final String regex, final String template,
                                          final String substringPrefix) {
        if (!matches(str, regex)) {
            return null;
        }

        String matchResult = template;
        Matcher matcher = Pattern.compile(regex).matcher(str);
        if (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                int index = i + 1;
                String matchValue = matcher.group(index);
                if (matchValue == null) {
                    matchValue = Globals.DEFAULT_VALUE_STRING;
                } else {
                    if (StringUtils.notBlank(substringPrefix) && matchValue.startsWith(substringPrefix)) {
                        matchValue = matchValue.substring(substringPrefix.length());
                    }
                }
                matchResult = replace(matchResult, "$" + index, matchValue);
            }

            return matchResult;
        }
        return str;
    }

    /**
     * Random string
     *
     * @param length string length
     * @return Random generate string
     */
    public static String randomString(final int length) {
        StringBuilder generateKey = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            generateKey.append(AUTHORIZATION_CODE_ITEMS.charAt(random.nextInt(AUTHORIZATION_CODE_ITEMS.length())));
        }
        return generateKey.toString();
    }

    /**
     * Random number string
     *
     * @param length string length
     * @return Random generate number string
     */
    public static String randomNumber(final int length) {
        StringBuilder generateKey = new StringBuilder();
        for (int i = 0; i < length; i++) {
            generateKey.append((char) (Math.random() * 10 + '0'));
        }
        return generateKey.toString();
    }

    /**
     * Random index char.
     *
     * @param beginIndex the beginning index
     * @param endIndex   the end index
     * @return the char
     */
    public static char randomIndex(final int beginIndex, final int endIndex) {
        return (char) (Math.random() * (endIndex - beginIndex + 1) + beginIndex + '0');
    }

    /**
     * Random char char.
     *
     * @param beginIndex the beginning index
     * @param endIndex   the end index
     * @return the char
     */
    public static char randomChar(final int beginIndex, final int endIndex) {
        return (char) (Math.random() * (endIndex - beginIndex + 1) + beginIndex + 'a');
    }

    /**
     * Escape url address
     *
     * @param str original url address
     * @return escape url address
     */
    public static String escape(final String str) {
        int length;
        char ch;
        StringBuilder StringBuilder = new StringBuilder();

        StringBuilder.ensureCapacity(str.length() * 6);

        for (length = 0; length < str.length(); length++) {
            ch = str.charAt(length);

            if (Character.isDigit(ch) || Character.isLowerCase(ch) || Character.isUpperCase(ch)) {
                StringBuilder.append(ch);
            } else if (length < 256) {
                StringBuilder.append("%");
                StringBuilder.append(Integer.toString(ch, 16));
            } else {
                StringBuilder.append("%u");
                StringBuilder.append(Integer.toString(ch, 16));
            }
        }

        return StringBuilder.toString();
    }

    /**
     * Unescape url address
     *
     * @param str escaped url address string
     * @return unescape url address
     */
    public static String unescape(final String str) {
        if (str == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        StringBuilder StringBuilder = new StringBuilder();
        StringBuilder.ensureCapacity(str.length());
        int lastIndex = 0;
        int index;
        char ch;
        while (lastIndex < str.length()) {
            index = str.indexOf("%", lastIndex);
            if (index == lastIndex) {
                if (str.charAt(index + 1) == 'u') {
                    ch = (char) Integer.parseInt(str.substring(index + 2, index + 6), 16);
                    StringBuilder.append(ch);
                    lastIndex = index + 6;
                } else {
                    ch = (char) Integer.parseInt(str.substring(index + 1, index + 3), 16);
                    StringBuilder.append(ch);
                    lastIndex = index + 3;
                }
            } else {
                if (index == -1) {
                    StringBuilder.append(str.substring(lastIndex));
                    lastIndex = str.length();
                } else {
                    StringBuilder.append(str, lastIndex, index);
                    lastIndex = index;
                }
            }
        }
        return StringBuilder.toString();
    }

    /**
     * Check given character is space
     *
     * @param letter character
     * @return check result
     */
    public static boolean isSpace(final char letter) {
        return (letter == 8 || letter == 9 || letter == 10 || letter == 13 || letter == 32 || letter == 160);
    }

    /**
     * Check given character is English character
     *
     * @param letter character
     * @return check result
     */
    public static boolean isEnglish(final char letter) {
        return (letter > 'a' && letter < 'z') || (letter > 'A' && letter < 'Z');
    }

    /**
     * Check given character is number
     *
     * @param letter character
     * @return check result
     */
    public static boolean isNumber(final char letter) {
        return letter >= '0' && letter <= '9';
    }

    /**
     * Check given character is Chinese/Japanese/Korean
     *
     * @param character character
     * @return check result
     */
    public static boolean isCJK(final char character) {
        UnicodeBlock unicodeBlock = UnicodeBlock.of(character);

        return (unicodeBlock == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || unicodeBlock == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || unicodeBlock == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || unicodeBlock == UnicodeBlock.GENERAL_PUNCTUATION
                || unicodeBlock == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                //全角数字字符和日韩字符
                || unicodeBlock == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                //韩文字符集
                || unicodeBlock == UnicodeBlock.HANGUL_SYLLABLES
                || unicodeBlock == UnicodeBlock.HANGUL_JAMO
                || unicodeBlock == UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
                //日文字符集
                || unicodeBlock == UnicodeBlock.HIRAGANA //平假名
                || unicodeBlock == UnicodeBlock.KATAKANA //片假名
                || unicodeBlock == UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS);
    }

    /**
     * Check type class is simple data class, e.g. Number(include int, Integer, long, Long...), String, boolean and Date
     *
     * @param typeClass type class
     * @return check result
     */
    public static boolean simpleDataType(final Class<?> typeClass) {
        return SIMPLE_DATA_TYPES.contains(ObjectUtils.retrieveSimpleDataType(typeClass));
    }

    /**
     * Parse simple data to target class
     *
     * @param <T>       the type parameter
     * @param dataValue value
     * @param typeClass target define class
     * @return target object
     * @throws ParseException given string is null
     */
    public static <T> T parseSimpleData(final String dataValue, final Class<T> typeClass) throws ParseException {
        Object paramObj = null;
        if (dataValue == null || typeClass == null || Globals.DEFAULT_VALUE_STRING.equals(dataValue)) {
            return null;
        }

        if (BeanObject.class.isAssignableFrom(typeClass)) {
            paramObj = stringToObject(dataValue, typeClass);
        } else {
            DataType dataType = ObjectUtils.retrieveSimpleDataType(typeClass);

            switch (dataType) {
                case BOOLEAN -> paramObj = Boolean.valueOf(dataValue);
                case DATE -> paramObj = DateTimeUtils.parseSiteMapDate(dataValue);
                case ENUM -> paramObj = ReflectionUtils.parseEnum(typeClass).get(dataValue);
                case NUMBER -> {
                    if (typeClass.equals(Integer.class) || typeClass.equals(int.class)) {
                        paramObj = Integer.valueOf(dataValue);
                    } else if (typeClass.equals(Float.class) || typeClass.equals(float.class)) {
                        paramObj = Float.valueOf(dataValue);
                    } else if (typeClass.equals(Double.class) || typeClass.equals(double.class)) {
                        paramObj = Double.valueOf(dataValue);
                    } else if (typeClass.equals(Short.class) || typeClass.equals(short.class)) {
                        paramObj = Short.valueOf(dataValue);
                    } else if (typeClass.equals(Long.class) || typeClass.equals(long.class)) {
                        paramObj = Long.valueOf(dataValue);
                    } else if (typeClass.equals(BigInteger.class)) {
                        paramObj = new BigInteger(dataValue);
                    }
                }
                case CDATA -> paramObj = StringUtils.formatForText(dataValue).toCharArray();
                case BINARY -> paramObj = StringUtils.base64Decode(
                        StringUtils.replace(dataValue, " ", Globals.DEFAULT_VALUE_STRING));
                default -> paramObj = StringUtils.formatForText(dataValue);
            }
        }

        return typeClass.cast(paramObj);
    }

    /**
     * Validate given code, support China ID Code, China Social Credit Code, Luhn Algorithm
     *
     * @param code     Code
     * @param codeType Code type
     * @return Validate result
     */
    public static boolean validateCode(final String code, final CodeType codeType) {
        if (StringUtils.isEmpty(code)) {
            return Boolean.FALSE;
        }
        switch (codeType) {
            case CHN_ID_Code -> {
                String cardCode = code.toUpperCase();
                if (StringUtils.matches(cardCode, RegexGlobals.CHN_ID_Card)) {
                    int validateCode = CHN_ID_CARD_CODE.indexOf(cardCode.charAt(17));
                    if (validateCode != -1) {
                        int sigma = 0;
                        for (int i = 0; i < 17; i++) {
                            sigma += Character.digit(cardCode.charAt(i), 10) * (Math.pow(2, 17 - i) % 11);
                        }
                        return validateCode == ((12 - (sigma % 11)) % 11);
                    }
                }
            }
            case CHN_Social_Code -> {
                String creditCode = code.toUpperCase();
                if (StringUtils.matches(creditCode, RegexGlobals.CHN_Social_Credit)) {
                    int validateCode = CHN_SOCIAL_CREDIT_CODE.indexOf(creditCode.charAt(17));
                    if (validateCode != -1) {
                        int sigma = 0;
                        for (int i = 0; i < 17; i++) {
                            sigma += CHN_SOCIAL_CREDIT_CODE.indexOf(creditCode.charAt(i)) * (Math.pow(3, i) % 31);
                        }

                        int authCode = 31 - (sigma % 31);
                        return (authCode == 31) ? (validateCode == 0) : (authCode == validateCode);
                    }
                }
            }
            case Luhn -> {
                if (StringUtils.matches(code, RegexGlobals.LUHN)) {
                    int result = 0, length = code.length();
                    for (int i = 0; i < length; i++) {
                        int currentCode = Character.getNumericValue(code.charAt(length - i - 1));
                        if (i % 2 == 1) {
                            currentCode *= 2;
                            if (currentCode > 9) {
                                currentCode -= 9;
                            }
                        }
                        result += currentCode;
                    }
                    return result % 10 == 0;
                }
            }
        }
        return Boolean.FALSE;
    }

    private static <T> T stringToObject(final String string, final StringType stringType, final String encoding,
                                        final Class<T> beanClass, final String... schemaPath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parse string: {} use encoding: {} to bean: {}", string, encoding, beanClass.getName());
        }

        if (StringType.SIMPLE.equals(stringType)) {
            try {
                return parseSimpleData(string, beanClass);
            } catch (ParseException e) {
                LOGGER.error("Parse simple error! ");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Stack message: ", e);
                }
            }
        }
        String stringEncoding = (encoding == null) ? Globals.DEFAULT_ENCODING : encoding;
        try (InputStream inputStream = new ByteArrayInputStream(string.getBytes(stringEncoding))) {
            return streamToObject(inputStream, stringType, beanClass, schemaPath);
        } catch (IOException e) {
            LOGGER.error("Parse string error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
        }
        return null;
    }

    private static String changeFirstCharacterCase(final String str, final boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuilder buf = new StringBuilder(str.length());
        if (capitalize) {
            buf.append(Character.toUpperCase(str.charAt(0)));
        } else {
            buf.append(Character.toLowerCase(str.charAt(0)));
        }
        buf.append(str.substring(1));
        return buf.toString();
    }

    /**
     * The enum Code type.
     */
    public enum CodeType {
        /**
         * Chn social code type.
         */
        CHN_Social_Code,
        /**
         * Chn id code type.
         */
        CHN_ID_Code,
        /**
         * Luhn code type.
         */
        Luhn
    }

    private static final class SchemaResourceResolver implements LSResourceResolver {

        @Override
        public LSInput resolveResource(final String type, final String namespaceURI, final String publicId,
                                       final String systemId, final String baseURI) {
            LOGGER.debug("Resolving TYPE: {}, NAMESPACE_URI: {}, PUBLIC_ID: {}, SYSTEM_ID: {}. BASE_URI: {}",
                    type, namespaceURI, publicId, systemId, baseURI);
            String schemaLocation = baseURI.substring(0, baseURI.lastIndexOf("/") + 1);
            String filePath;
            if (SCHEMA_MAPPING.containsKey(namespaceURI)) {
                filePath = SCHEMA_MAPPING.get(namespaceURI);
            } else {
                if (!systemId.contains(Globals.HTTP_PROTOCOL)) {
                    filePath = schemaLocation + systemId;
                } else {
                    filePath = systemId;
                }
            }
            try {
                return new LSInputImpl(publicId, namespaceURI, FileUtils.loadFile(filePath));
            } catch (IOException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Load schema error! ", e);
                }
                return new LSInputImpl();
            }
        }
    }

    private static final class LSInputImpl implements LSInput {
        private String publicId;
        private String systemId;
        private String baseURI;
        private InputStream byteStream;
        private Reader characterStream;
        private String stringData;
        private String encoding;
        private boolean certifiedText;

        LSInputImpl() {

        }

        LSInputImpl(final String publicId, final String systemId, final InputStream byteStream) {
            this.publicId = publicId;
            this.systemId = systemId;
            this.byteStream = byteStream;
        }

        @Override
        public String getPublicId() {
            return publicId;
        }

        @Override
        public void setPublicId(String publicId) {
            this.publicId = publicId;
        }

        @Override
        public String getSystemId() {
            return systemId;
        }

        @Override
        public void setSystemId(String systemId) {
            this.systemId = systemId;
        }

        @Override
        public String getBaseURI() {
            return baseURI;
        }

        @Override
        public void setBaseURI(String baseURI) {
            this.baseURI = baseURI;
        }

        @Override
        public InputStream getByteStream() {
            return byteStream;
        }

        @Override
        public void setByteStream(InputStream byteStream) {
            this.byteStream = byteStream;
        }

        @Override
        public Reader getCharacterStream() {
            return characterStream;
        }

        @Override
        public void setCharacterStream(Reader characterStream) {
            this.characterStream = characterStream;
        }

        @Override
        public String getStringData() {
            return stringData;
        }

        @Override
        public void setStringData(String stringData) {
            this.stringData = stringData;
        }

        @Override
        public String getEncoding() {
            return encoding;
        }

        @Override
        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        @Override
        public boolean getCertifiedText() {
            return certifiedText;
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {
            this.certifiedText = certifiedText;
        }
    }
}
