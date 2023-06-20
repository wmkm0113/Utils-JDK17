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
package org.nervousync.commons.http.header;

/**
 * Simple header of request
 *
 * @param headerName  Header name
 * @param headerValue Header value
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 4, 2018 12:15:18 PM $
 */
public record SimpleHeader(String headerName, String headerValue) {

    /**
     * Default constructor
     *
     * @param headerName  Header name
     * @param headerValue Header value
     */
    public SimpleHeader {
    }

    /**
     * @return the headerName
     */
    @Override
    public String headerName() {
        return headerName;
    }

    /**
     * @return the headerValue
     */
    @Override
    public String headerValue() {
        return headerValue;
    }
}
