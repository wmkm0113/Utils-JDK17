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
package org.nervousync.security.crypto.impl;

import org.nervousync.commons.Globals;
import org.nervousync.security.config.CipherConfig;
import org.nervousync.security.crypto.AsymmetricCryptoAdapter;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;

/**
 * <h2 class="en">Asymmetric RSA crypto adapter class</h2>
 * <h2 class="zh-CN">RSA非对称加密解密适配器的实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2012 14:27:46 $
 */
public final class RSACryptoAdapterImpl extends AsymmetricCryptoAdapter {

    /**
     * <h3 class="en">Constructor for RSACryptoAdapterImpl</h3>
     * <h3 class="zh-CN">RSA非对称加密解密适配器的实现类的构造方法</h3>
     *
     * @param cipherConfig  <span class="en">Cipher configure</span>
     *                      <span class="zh-CN">密码设置</span>
     * @param cryptoMode    <span class="en">Crypto mode</span>
     *                      <span class="zh-CN">加密解密模式</span>
     * @param cipherKey     <span class="en">Crypto key</span>
     *                      <span class="zh-CN">加密解密密钥</span>
     *
     * @throws CryptoException
     * <span class="en">If an error occurs when initialize cipher</span>
     * <span class="zh-CN">当初始化加密解密实例对象时出现异常</span>
     */
    public RSACryptoAdapterImpl(final CipherConfig cipherConfig, final CryptoMode cryptoMode,
                                final CipherKey cipherKey) throws CryptoException {
        super(cipherConfig, cryptoMode, cipherKey, PADDING_LENGTH(cryptoMode, cipherConfig.padding()));
    }

    private static int PADDING_LENGTH(final CryptoMode cryptoMode, final String padding) {
        if (CryptoMode.ENCRYPT.equals(cryptoMode) || CryptoMode.DECRYPT.equals(cryptoMode)) {
            return switch (padding) {
                case "PKCS1Padding" -> 11;
                case "OAEPWithMD5AndMGF1Padding" -> 34;
                case "OAEPPadding", "OAEPWithSHA-1AndMGF1Padding" -> 42;
                case "OAEPWithSHA3-224AndMGF1Padding", "OAEPWithSHA-224AndMGF1Padding" -> 58;
                case "OAEPWithSHA3-256AndMGF1Padding", "OAEPWithSHA-256AndMGF1Padding" -> 66;
                case "OAEPWithSHA3-384AndMGF1Padding", "OAEPWithSHA-384AndMGF1Padding" -> 98;
                case "OAEPWithSHA3-512AndMGF1Padding", "OAEPWithSHA-512AndMGF1Padding" -> 130;
                default -> 0;
            };
        }
        return Globals.INITIALIZE_INT_VALUE;
    }
}
