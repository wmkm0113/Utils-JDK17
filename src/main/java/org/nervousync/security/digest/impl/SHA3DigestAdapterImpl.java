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
package org.nervousync.security.digest.impl;

import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.digest.*;
import org.nervousync.security.digest.BaseDigestAdapter;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.StringUtils;

import java.security.MessageDigest;

/**
 * <h2 class="en">Symmetric SHA3 crypto adapter class</h2>
 * <h2 class="zh-CN">SHA3摘要算法适配器的实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jan 13, 2012 13:56:12 $
 */
public final class SHA3DigestAdapterImpl extends BaseDigestAdapter {
    /**
     * <h3 class="en">Constructor for SHA3DigestAdapterImpl</h3>
     * <h3 class="zh-CN">SHA3摘要算法适配器实现类类的构造方法</h3>
     *
     * @param algorithm     <span class="en">Cipher Algorithm</span>
     *                      <span class="zh-CN">密码算法</span>
     *
     * @throws CryptoException
     * <span class="en">If an error occurs when initialize adaptor</span>
     * <span class="zh-CN">当初始化适配器时出现异常</span>
     */
    public SHA3DigestAdapterImpl(String algorithm) throws CryptoException {
        super(algorithm, new byte[0]);
    }
    /**
     * <h3 class="en">Constructor for SHA3DigestAdapterImpl</h3>
     * <h3 class="zh-CN">SHA3摘要算法适配器实现类类的构造方法</h3>
     *
     * @param algorithm     <span class="en">Cipher Algorithm</span>
     *                      <span class="zh-CN">密码算法</span>
     * @param keyBytes      <span class="en">Hmac key data bytes</span>
     *                      <span class="zh-CN">消息认证码算法密钥数据数组</span>
     *
     * @throws CryptoException
     * <span class="en">If an error occurs when initialize adaptor</span>
     * <span class="zh-CN">当初始化适配器时出现异常</span>
     */
    public SHA3DigestAdapterImpl(String algorithm, byte[] keyBytes) throws CryptoException {
        super(algorithm, keyBytes);
    }
    /**
	 * <h3 class="en">Abstract method for initialize MessageDigest instance</h3>
	 * <h3 class="zh-CN">抽象方法用于初始化消息摘要算法适配器实例对象</h3>
     *
     * @param algorithm     <span class="en">Cipher Algorithm</span>
     *                      <span class="zh-CN">密码算法</span>
     *
     * @return  <span class="en">Initialized MessageDigest instance</span>
     *          <span class="zh-CN">初始化的消息摘要算法适配器</span>
     */
    @Override
    protected MessageDigest initDigest(String algorithm) throws CryptoException {
        if (StringUtils.isEmpty(algorithm)) {
            throw new CryptoException(0x00000015000DL, "Utils", "Unknown_Algorithm_Digits_Error");
        }
        return switch (algorithm.toUpperCase()) {
            case "SHA3-224" -> new SHA3.Digest224();
            case "SHA3-256" -> new SHA3.Digest256();
            case "SHA3-384" -> new SHA3.Digest384();
            case "SHA3-512" -> new SHA3.Digest512();
            case "SHAKE128" -> new SHA3.DigestShake128_256();
            case "SHAKE256" -> new SHA3.DigestShake256_512();
            default -> throw new CryptoException(0x00000015000DL, "Utils", "Unknown_Algorithm_Digits_Error", algorithm);
        };
    }
    /**
	 * <h3 class="en">Abstract method for initialize Hmac instance</h3>
	 * <h3 class="zh-CN">抽象方法用于初始化消息认证码适配器实例对象</h3>
     *
     * @param algorithm     <span class="en">Cipher Algorithm</span>
     *                      <span class="zh-CN">密码算法</span>
     * @param keyBytes      <span class="en">Hmac key data bytes</span>
     *                      <span class="zh-CN">消息认证码算法密钥数据数组</span>
     *
     * @return  <span class="en">Initialized Hmac instance</span>
     *          <span class="zh-CN">初始化的消息认证码算法适配器</span>
     */
    @Override
    protected Mac initHmac(String algorithm, byte[] keyBytes) throws CryptoException {
        if (StringUtils.isEmpty(algorithm) || !algorithm.toUpperCase().endsWith("HMAC")) {
            throw new CryptoException(0x00000015000DL, "Utils", "Unknown_Algorithm_Digits_Error", algorithm);
        }
        HMac hmac = switch (algorithm.toUpperCase()) {
            case "SHA3-224/HMAC" -> new HMac(new SHA3Digest(224));
            case "SHA3-256/HMAC" -> new HMac(new SHA3Digest(256));
            case "SHA3-384/HMAC" -> new HMac(new SHA3Digest(384));
            case "SHA3-512/HMAC" -> new HMac(new SHA3Digest(512));
            default -> throw new CryptoException(0x00000015000DL, "Utils", "Unknown_Algorithm_Digits_Error", algorithm);
        };
        hmac.init(new KeyParameter(keyBytes));
        return hmac;
    }
}
