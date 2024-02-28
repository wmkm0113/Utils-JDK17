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

package org.nervousync.beans.roles;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.enumerations.core.ConnectionCode;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public final class CheckRoles extends BeanObject {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
    @Serial
    private static final long serialVersionUID = -8504204388427452709L;

    @JsonProperty("connection")
    private ConnectionCode connectionCode;
    @JsonProperty("codes")
    private List<String> roleCodeList = new ArrayList<>();

    public CheckRoles() {
    }

    public ConnectionCode getConnectionCode() {
        return connectionCode;
    }

    public void setConnectionCode(ConnectionCode connectionCode) {
        this.connectionCode = connectionCode;
    }

    public List<String> getRoleCodeList() {
        return roleCodeList;
    }

    public void setRoleCodeList(List<String> roleCodeList) {
        this.roleCodeList = roleCodeList;
    }
}
