/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
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
/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
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
package org.nervousync.zip.models.header.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.nervousync.commons.Globals;
import org.nervousync.exceptions.utils.DataInvalidException;
import org.nervousync.utils.StringUtils;
import org.nervousync.zip.models.header.LocalFileHeader;
import org.nervousync.exceptions.zip.ZipException;
import org.nervousync.utils.RawUtils;

/**
 * The type Header operator.
 *
 * @author Steven Wee   <a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Dec 2, 2017 6:07:27 PM $
 */
public final class HeaderOperator {

	/**
	 * Retrieve salt length int.
	 *
	 * @param aesStrength the aes strength
	 * @return the int
	 * @throws ZipException the zip exception
	 */
	public static int saltLength(int aesStrength) throws ZipException {
		return switch (aesStrength) {
			case Globals.AES_STRENGTH_128 -> 8;
			case Globals.AES_STRENGTH_192 -> 12;
			case Globals.AES_STRENGTH_256 -> 16;
			default -> throw new ZipException("unable to determine salt length: invalid aes key strength");
		};
	}

	/**
	 * Write extended local header int.
	 *
	 * @param localFileHeader the local file header
	 * @param outputStream    the output stream
	 * @return the int
	 * @throws ZipException the zip exception
	 * @throws IOException  the io exception
	 */
	public static int writeExtendedLocalHeader(LocalFileHeader localFileHeader, OutputStream outputStream)
			throws ZipException, DataInvalidException, IOException {
		if (localFileHeader == null || outputStream == null) {
			throw new ZipException("input parameters is null, cannot write extended local header");
		}

		List<String> byteArrayList = new ArrayList<>();
		byte[] intBuffer = new byte[4];

		// Extended local file header signature
		RawUtils.writeInt(intBuffer, ByteOrder.LITTLE_ENDIAN, (int) Globals.EXTSIG);
		copyByteArrayToList(intBuffer, byteArrayList);

		// CRC
		RawUtils.writeInt(intBuffer, ByteOrder.LITTLE_ENDIAN, (int) localFileHeader.getCrc32());
		copyByteArrayToList(intBuffer, byteArrayList);

		// Compressed size
		long compressedSize = localFileHeader.getCompressedSize();
		if (compressedSize > Integer.MAX_VALUE) {
			compressedSize = Integer.MAX_VALUE;
		}
		RawUtils.writeInt(intBuffer, ByteOrder.LITTLE_ENDIAN, (int) compressedSize);
		copyByteArrayToList(intBuffer, byteArrayList);

		// Original size
		long originalSize = localFileHeader.getOriginalSize();
		if (originalSize > Integer.MAX_VALUE) {
			originalSize = Integer.MAX_VALUE;
		}
		RawUtils.writeInt(intBuffer, ByteOrder.LITTLE_ENDIAN, (int) originalSize);
		copyByteArrayToList(intBuffer, byteArrayList);

		byte[] extendLocationHdrBytes = convertByteArrayListToByteArray(byteArrayList);
		outputStream.write(extendLocationHdrBytes);
		return extendLocationHdrBytes.length;
	}

	/**
	 * Convert the byte array list to byte array byte [ ].
	 *
	 * @param arrayList the array list
	 * @return the byte [ ]
	 * @throws ZipException the zip exception
	 */
	public static byte[] convertByteArrayListToByteArray(List<String> arrayList) throws ZipException {
		if (arrayList == null) {
			throw new ZipException("input byte array list is null, cannot convert to byte array");
		}

		if (!arrayList.isEmpty()) {
			byte[] returnBytes = new byte[arrayList.size()];

			for (int i = 0; i < arrayList.size(); i++) {
				returnBytes[i] = Byte.parseByte(arrayList.get(i));
			}

			return returnBytes;
		}

		return new byte[0];
	}

	/**
	 * Append short to the array list.
	 *
	 * @param value     the value
	 * @param arrayList the array list
	 * @throws ZipException the zip exception
	 */
	public static void appendShortToArrayList(short value, List<String> arrayList)
			throws ZipException, DataInvalidException {
		byte[] shortBuffer = new byte[2];
		RawUtils.writeShort(shortBuffer, ByteOrder.LITTLE_ENDIAN, value);
		HeaderOperator.copyByteArrayToList(shortBuffer, arrayList);
	}

	/**
	 * Append int to the array list.
	 *
	 * @param value     the value
	 * @param arrayList the array list
	 * @throws ZipException the zip exception
	 */
	public static void appendIntToArrayList(int value, List<String> arrayList)
			throws ZipException, DataInvalidException {
		byte[] intBuffer = new byte[4];
		RawUtils.writeInt(intBuffer, ByteOrder.LITTLE_ENDIAN, value);
		HeaderOperator.copyByteArrayToList(intBuffer, arrayList);
	}

	/**
	 * Append long to the array list.
	 *
	 * @param value     the value
	 * @param arrayList the array list
	 * @throws ZipException the zip exception
	 */
	public static void appendLongToArrayList(long value, List<String> arrayList)
			throws ZipException, DataInvalidException {
		byte[] longBuffer = new byte[8];
		RawUtils.writeLong(longBuffer, ByteOrder.LITTLE_ENDIAN, value);
		HeaderOperator.copyByteArrayToList(longBuffer, arrayList);
	}

	/**
	 * Copy byte arrays to list.
	 *
	 * @param byteArray the byte array
	 * @param arrayList the array list
	 * @throws ZipException the zip exception
	 */
	public static void copyByteArrayToList(byte[] byteArray, List<String> arrayList) throws ZipException {
		if (arrayList == null || byteArray == null) {
			throw new ZipException("one of the input parameters is null, cannot copy byte array to array list");
		}

		for (byte b : byteArray) {
			arrayList.add(Byte.toString(b));
		}
	}

	/**
	 * Copy entry byte arrays to list.
	 *
	 * @param entryPath the entry path
	 * @param arrayList the array list
	 * @throws ZipException the zip exception
	 */
	public static void copyByteArrayToList(String entryPath, List<String> arrayList) throws ZipException {
		copyByteArrayToList(convertCharset(entryPath), arrayList);
	}

	/**
	 * Convert charset byte [ ].
	 *
	 * @param string the string
	 * @return the byte [ ]
	 * @throws ZipException the zip exception
	 */
	private static byte[] convertCharset(String string) throws ZipException {
		try {
			byte[] converted;
			String charSet = StringUtils.detectCharset(string);
			converted = switch (charSet) {
				case Globals.CHARSET_CP850 -> string.getBytes(Globals.CHARSET_CP850);
				case Globals.CHARSET_GBK -> string.getBytes(Globals.CHARSET_GBK);
				case Globals.DEFAULT_ENCODING -> string.getBytes(Globals.DEFAULT_ENCODING);
				default -> string.getBytes(Globals.DEFAULT_SYSTEM_CHARSET);
			};
			return converted;
		} catch (UnsupportedEncodingException err) {
			return string.getBytes(Charset.defaultCharset());
		} catch (Exception e) {
			throw new ZipException("", e);
		}
	}
}
