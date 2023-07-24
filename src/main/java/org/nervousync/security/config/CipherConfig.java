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
package org.nervousync.security.config;

import java.io.Serial;
import java.io.Serializable;

/**
 * <h2 class="en">Cipher configure</h2>
 * <h2 class="zh-CN">密码设置</h2>
 *
 * @param algorithm <span class="en">Cipher Algorithm</span>
 *                  <span class="zh-CN">密码算法</span>
 * @param mode      <span class="en">Cipher Mode</span>
 *                  <span class="zh-CN">分组密码模式</span>
 *                  Cipher Mode
 * @param padding   <span class="en">Padding Mode</span>
 *                  <span class="zh-CN">数据填充模式</span>
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2016 15:47:22 $
 */
public record CipherConfig(String algorithm, String mode, String padding) implements Serializable {
    /**
     * <span class="en">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    @Serial
    private static final long serialVersionUID = -2132901674474697239L;

    /**
     * <h3 class="en">Constructor method for CipherConfig</h3>
     * <h3 class="zh-CN">密码设置的构造方法</h3>
     *
     * @param algorithm <span class="en">Cipher Algorithm</span>
     *                  <span class="zh-CN">密码算法</span>
     * @param mode      <span class="en">Cipher Mode</span>
     *                  <span class="zh-CN">分组密码模式</span>
     * @param padding   <span class="en">Padding Mode</span>
     *                  <span class="zh-CN">数据填充模式</span>
     */
    public CipherConfig {
    }

    /**
     * <h3 class="en">Getter method for Cipher Algorithm</h3>
     * <h3 class="zh-CN">密码算法的Getter方法</h3>
     *
     * @return <span class="en">Cipher Algorithm</span>
     * <span class="zh-CN">密码算法</span>
     */
    @Override
    public String algorithm() {
        return algorithm;
    }

    /**
     * <h3 class="en">Getter method for Cipher Mode</h3>
     * <h3 class="zh-CN">分组密码模式的Getter方法</h3>
     *
     * @return <span class="en">Cipher Mode</span>
     * <span class="zh-CN">分组密码模式</span>
     */
    @Override
    public String mode() {
        return mode;
    }

    /**
     * <h3 class="en">Getter method for Padding Mode</h3>
     * <h3 class="zh-CN">数据填充模式的Getter方法</h3>
     *
     * @return <span class="en">Padding Mode</span>
     * <span class="zh-CN">数据填充模式</span>
     */
    @Override
    public String padding() {
        return padding;
    }

    /**
     * <h3 class="en">Convert current cipher configure to string</h3>
     * <h3 class="zh-CN">转换当前密码配置信息为字符串</h3>
     *
     * @return <span class="en">Converted string</span>
     * <span class="zh-CN">转换后的字符串</span>
     */
    @Override
    public String toString() {
        return String.join("/", this.algorithm, this.mode, this.padding);
    }
}
