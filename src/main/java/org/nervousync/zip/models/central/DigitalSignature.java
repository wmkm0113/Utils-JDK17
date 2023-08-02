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
package org.nervousync.zip.models.central;

/**
 * The type Digital signature.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 28, 2017 4:38:01 PM $
 */
public final class DigitalSignature {

	private int signature;
	private int dataSize;
	private String signatureData;

	/**
	 * Instantiates a new Digital signature.
	 */
	public DigitalSignature() {
	}

	/**
	 * Gets signature.
	 *
	 * @return the signature
	 */
	public int getSignature() {
		return signature;
	}

	/**
	 * Sets signature.
	 *
	 * @param signature the signature to set
	 */
	public void setSignature(int signature) {
		this.signature = signature;
	}

	/**
	 * Gets data size.
	 *
	 * @return the dataSize
	 */
	public int getDataSize() {
		return dataSize;
	}

	/**
	 * Sets data size.
	 *
	 * @param dataSize the dataSize to set
	 */
	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	/**
	 * Gets signature data.
	 *
	 * @return the signatureData
	 */
	public String getSignatureData() {
		return signatureData;
	}

	/**
	 * Sets signature data.
	 *
	 * @param signatureData the signatureData to set
	 */
	public void setSignatureData(String signatureData) {
		this.signatureData = signatureData;
	}
}
