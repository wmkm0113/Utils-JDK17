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
package org.nervousync.enumerations.beans;

/**
 * <h2 class="en-US">JavaBean Property Data Flow Enumerations</h2>
 * <h2 class="zh-CN">JavaBean属性值数据流向枚举</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jun 25, 2023 14:28:27 $
 */
public enum DataFlow {
    /**
     * <span class="en-US">Property value from the target JavaBean instance to current JavaBean instance</span>
     * <span class="zh-CN">属性值从目标JavaBean实例复制到当前JavaBean实例</span>
     */
    IN,
    /**
     * <span class="en-US">Property value from current JavaBean instance to the target JavaBean instance</span>
     * <span class="zh-CN">属性值从当前JavaBean实例复制到目标JavaBean实例</span>
     */
    OUT
}
