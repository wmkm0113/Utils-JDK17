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
package org.nervousync.mail.config.builder;

import org.nervousync.builder.AbstractBuilder;
import org.nervousync.commons.Globals;
import org.nervousync.commons.RegexGlobals;
import org.nervousync.proxy.AbstractProxyConfigBuilder;
import org.nervousync.proxy.ProxyConfig;
import org.nervousync.enumerations.mail.MailProtocol;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.mail.config.MailConfig;
import org.nervousync.security.factory.SecureConfig;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.StringUtils;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Optional;

/**
 * <h2 class="en">Abstract mail configure builder for Generics Type</h2>
 * <p class="en">
 *     Current abstract class is using to integrate to another builder
 *     which configure contains mail configure information.
 * </p>
 * <h2 class="zh-CN">拥有父构造器的电子邮件配置信息抽象构造器</h2>
 * <p class="zh-CN">当前抽象构建器用于整合到包含邮件配置信息的其他配置构建器</p>
 *
 * @param <T>   <span class="en">Generics Type Class</span>
 *              <span class="zh-CN">泛型类</span>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jul 31, 2022 16:27:18 $
 */
public abstract class AbstractMailConfigBuilder<T> extends AbstractBuilder<T> {
    /**
     * <h2 class="en">Current mail configure information</h2>
     * <h2 class="zh-CN">当前邮件配置信息</h2>
     */
    protected final MailConfig mailConfig;
    /**
     * <h3 class="en">Protected constructor for AbstractMailConfigBuilder</h3>
     * <h3 class="zh-CN">AbstractMailConfigBuilder的构造函数</h3>
     *
     * @param parentBuilder     <span class="en">Generics Type instance</span>
     *                          <span class="zh-CN">泛型类实例对象</span>
     * @param mailConfig        <span class="en">Mail configure information</span>
     *                          <span class="zh-CN">邮件配置信息</span>
     */
    protected AbstractMailConfigBuilder(final T parentBuilder, final MailConfig mailConfig) {
        super(parentBuilder);
        this.mailConfig = (mailConfig == null) ? new MailConfig() : mailConfig;
    }
    /**
     * <h3 class="en">Configure secure name</h3>
     * <span class="en">Builder will decrypt (if current configure contains secure name) and encrypt (using new secure name) password automatically</span>
     * <h3 class="zh-CN">设置安全名称</h3>
     * <span class="en">构造器会解密（如果当前配置信息中包含安全名称）并加密（使用新的安全名称）密码信息</span>
     *
     * @param secureName    <span class="en">New secure name</span>
     *                      <span class="zh-CN">新的安全名称</span>
     *
     * @return  <span class="en">Current builder instance</span>
     *          <span class="zh-CN">当前构造器实例对象</span>
     */
    public AbstractMailConfigBuilder<T> secureName(final String secureName) {
        if (StringUtils.notBlank(secureName) && SecureFactory.registeredConfig(secureName)) {
            if (StringUtils.notBlank(this.mailConfig.getPassword())) {
                String newPassword =
                        SecureFactory.update(this.mailConfig.getPassword(), this.mailConfig.getSecureName(), secureName);
                this.mailConfig.setPassword(newPassword);
            }
            Optional.ofNullable(this.mailConfig.getProxyConfig())
                    .filter(proxyConfig -> StringUtils.notBlank(proxyConfig.getPassword()))
                    .ifPresent(proxyConfig -> {
                        String newPassword = SecureFactory.update(proxyConfig.getPassword(),
                                this.mailConfig.getSecureName(), secureName);
                        proxyConfig.setPassword(newPassword);
                        this.mailConfig.setProxyConfig(proxyConfig);
                    });
            this.mailConfig.setSecureName(secureName);
        }
        return this;
    }
    /**
     * <h3 class="en">Configure custom secure config</h3>
     * <span class="en">
     *     Builder will decrypt (if current configure contains secure name or secure config)
     *     and encrypt (using new secure config) password automatically
     * </span>
     * <h3 class="zh-CN">设置安全名称</h3>
     * <span class="en">构造器会解密（如果当前配置信息中包含安全名称）并加密（使用新的安全配置）密码信息</span>
     *
     * @param secureName    <span class="en">New secure name</span>
     *                      <span class="zh-CN">新的安全名称</span>
     * @param secureConfig  <span class="en">Custom secure config</span>
     *                      <span class="zh-CN">新的安全配置</span>
     *
     * @return  <span class="en">Current builder instance</span>
     *          <span class="zh-CN">当前构造器实例对象</span>
     */
    public AbstractMailConfigBuilder<T> secureConfig(final String secureName, final SecureConfig secureConfig) {
        if (StringUtils.notBlank(secureName) && secureConfig != null) {
            if (StringUtils.notBlank(this.mailConfig.getPassword())) {
                String newPassword;
                if (SecureFactory.registeredConfig(secureName)) {
                    SecureFactory.register(Globals.DEFAULT_TEMPORARY_SECURE_NAME, secureConfig);
                    newPassword = SecureFactory.update(this.mailConfig.getPassword(), this.mailConfig.getSecureName(),
                            Globals.DEFAULT_TEMPORARY_SECURE_NAME);
                    SecureFactory.deregister(Globals.DEFAULT_TEMPORARY_SECURE_NAME);
                    SecureFactory.register(secureName, secureConfig);
                } else {
                    SecureFactory.register(secureName, secureConfig);
                    newPassword = SecureFactory.update(this.mailConfig.getPassword(), this.mailConfig.getSecureName(),
                            secureName);
                }
                this.mailConfig.setPassword(newPassword);
            }
            this.mailConfig.setSecureName(secureName);
            this.mailConfig.setSecureConfig(secureConfig);
        }
        return this;
    }
    /**
     * <h3 class="en">Configure authenticate information</h3>
     * <h3 class="zh-CN">设置身份认证信息</h3>
     *
     * @param userName  <span class="en">Mail account username</span>
     *                  <span class="zh-CN">邮件账户用户名</span>
     * @param password  <span class="en">Mail account password</span>
     *                  <span class="zh-CN">邮件账户密码</span>
     *
     * @return  <span class="en">Current builder instance</span>
     *          <span class="zh-CN">当前构造器实例对象</span>
     *
     * @throws BuilderException the builder exception
     * <span class="en">If username string not a valid e-mail address</span>
     * <span class="zh-CN">当用户名不是合法的电子邮件地址时</span>
     */
    public AbstractMailConfigBuilder<T> authentication(final String userName, final String password)
            throws BuilderException {
        if (!StringUtils.matches(userName, RegexGlobals.EMAIL_ADDRESS)) {
            throw new BuilderException(0x0000000E0001L, "Utils", "Username_Invalid_Mail_Error");
        }
        String encPassword = Globals.DEFAULT_VALUE_STRING;
        if (StringUtils.notBlank(password)) {
            if (StringUtils.notBlank(this.mailConfig.getSecureName())
                    && SecureFactory.registeredConfig(this.mailConfig.getSecureName())) {
                encPassword = SecureFactory.encrypt(this.mailConfig.getSecureName(), password);
            } else {
                encPassword = password;
            }
        }
        this.mailConfig.setUserName(userName);
        this.mailConfig.setPassword(encPassword);
        return this;
    }
    /**
     * <h3 class="en">Using current proxy configure information to create ProxyConfigBuilder instance</h3>
     * <h3 class="zh-CN">使用当前的代理服务器配置信息生成代理服务器配置构建器实例对象</h3>
     *
     * @return  <span class="en">ProxyConfigBuilder instance</span>
     *          <span class="zh-CN">代理服务器配置构建器实例对象</span>
     */
    public ProxyConfigBuilder<T> proxyConfig() {
        return new ProxyConfigBuilder<>(this, this.mailConfig.getSecureName(), this.mailConfig.getProxyConfig());
    }
    /**
     * <h3 class="en">Using current send server configure information to create ServerConfigBuilder instance</h3>
     * <h3 class="zh-CN">使用当前的发送邮件服务器配置信息生成邮件服务器配置构建器实例对象</h3>
     *
     * @return  <span class="en">ServerConfigBuilder instance</span>
     *          <span class="zh-CN">邮件服务器配置构建器实例对象</span>
     */
    public ServerConfigBuilder<T> sendConfig() {
        return new ServerConfigBuilder<>(this, Boolean.TRUE, this.mailConfig.getSendConfig());
    }
    /**
     * <h3 class="en">Using current receive server configure information to create ServerConfigBuilder instance</h3>
     * <h3 class="zh-CN">使用当前的接收邮件服务器配置信息生成邮件服务器配置构建器实例对象</h3>
     *
     * @return  <span class="en">ServerConfigBuilder instance</span>
     *          <span class="zh-CN">邮件服务器配置构建器实例对象</span>
     */
    public ServerConfigBuilder<T> receiveConfig() {
        return new ServerConfigBuilder<>(this, Boolean.FALSE, this.mailConfig.getReceiveConfig());
    }
    /**
     * <h3 class="en">Configure save path of mail attachment files</h3>
     * <h3 class="zh-CN">设置电子邮件附件的保存地址</h3>
     *
     * @param storagePath   <span class="en">Local save path</span>
     *                      <span class="zh-CN">本地保存地址</span>
     *
     * @return  <span class="en">ServerConfigBuilder instance</span>
     *          <span class="zh-CN">邮件服务器配置构建器实例对象</span>
     *
     * @throws BuilderException
     * <span class="en">If storage path is empty string or folder not exists</span>
     * <span class="zh-CN">如果本地保存地址为空字符串或目录不存在</span>
     */
    public AbstractMailConfigBuilder<T> storagePath(String storagePath) throws BuilderException {
        if (StringUtils.isEmpty(storagePath) || !FileUtils.isExists(storagePath)) {
            throw new BuilderException(0x0000000E0002L, "Utils", "Storage_Path_NotFound_Mail_Error");
        }
        this.mailConfig.setStoragePath(storagePath);
        return this;
    }

    /**
     * <h3 class="en">Configure the x509 certificate and private key for mail signature</h3>
     * <h3 class="zh-CN">设置用于电子邮件签名及验签的x509证书及私钥</h3>
     *
     * @param x509Certificate   <span class="en">x509 certificate using for verify signature</span>
     *                          <span class="zh-CN">x509证书，用于验证电子签名</span>
     * @param privateKey        <span class="en">Private key instance using for generate signature</span>
     *                          <span class="zh-CN">私钥对象实例，用于生成电子签名</span>
     *
     * @return  <span class="en">ServerConfigBuilder instance</span>
     *          <span class="zh-CN">邮件服务器配置构建器实例对象</span>
     */
    public AbstractMailConfigBuilder<T> signer(final X509Certificate x509Certificate, final PrivateKey privateKey) {
        if (x509Certificate != null && privateKey != null) {
            try {
                this.mailConfig.setCertificate(StringUtils.base64Encode(x509Certificate.getEncoded()));
                this.mailConfig.setPrivateKey(StringUtils.base64Encode(privateKey.getEncoded()));
            } catch (CertificateEncodingException e) {
                this.mailConfig.setCertificate(Globals.DEFAULT_VALUE_STRING);
                this.mailConfig.setPrivateKey(Globals.DEFAULT_VALUE_STRING);
            }
        }
        return this;
    }
    /**
     * <h3 class="en">Confirm proxy configure</h3>
     * <h3 class="zh-CN">确认代理服务器配置</h3>
     *
     * @param proxyConfig   <span class="en">Proxy configure information</span>
     *                      <span class="zh-CN">代理服务器配置</span>
     */
    private void proxyConfig(final ProxyConfig proxyConfig) {
        Optional.ofNullable(proxyConfig).ifPresent(this.mailConfig::setProxyConfig);
    }
    /**
     * <h3 class="en">Confirm mail server configure</h3>
     * <h3 class="zh-CN">确认邮件服务器配置</h3>
     *
     * @param sendConfig        <span class="en">Is send server configure</span>
     *                          <span class="zh-CN">是发送服务器配置信息</span>
     * @param serverConfig      <span class="en">Server configure information</span>
     *                          <span class="zh-CN">服务器配置</span>
     */
    private void serverConfig(final boolean sendConfig, final MailConfig.ServerConfig serverConfig) {
        Optional.ofNullable(serverConfig).ifPresent(config -> {
            if (sendConfig) {
                this.mailConfig.setSendConfig(config);
            } else {
                this.mailConfig.setReceiveConfig(config);
            }
        });
    }
    /**
     * <h2 class="en">Proxy configure builder</h2>
     * <h2 class="zh-CN">代理服务器配置信息抽象构造器</h2>
     *
     * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
     * @version $Revision: 1.0.0 $ $Date: Jul 31, 2022 16:33:28 $
     */
    public static final class ProxyConfigBuilder<T> extends AbstractProxyConfigBuilder<AbstractMailConfigBuilder<T>> {
        /**
         * <h3 class="en">Private constructor for ProxyConfigBuilder</h3>
         * <h3 class="zh-CN">ProxyConfigBuilder的私有构造函数</h3>
         *
         * @param parentBuilder     <span class="en">Mail configure builder instance</span>
         *                          <span class="zh-CN">电子邮件配置构造器实例</span>
         * @param secureName        <span class="en">New secure name</span>
         *                          <span class="zh-CN">新的安全名称</span>
         * @param proxyConfig       <span class="en">Proxy configure information</span>
         *                          <span class="zh-CN">代理服务器配置信息</span>
         */
        private ProxyConfigBuilder(final AbstractMailConfigBuilder<T> parentBuilder, final String secureName,
                                   final ProxyConfig proxyConfig) {
            super(parentBuilder, secureName, proxyConfig);
        }
        /**
         * <h2 class="en">Confirm current configure information</h2>
         * <h2 class="zh-CN">确认当前配置信息</h2>
         */
        protected void build() {
            super.parentBuilder.proxyConfig(this.proxyConfig);
        }
    }

    /**
     * <h2 class="en">Mail server configure builder</h2>
     * <h2 class="zh-CN">电子邮件服务器配置信息抽象构造器</h2>
     *
     * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
     * @version $Revision: 1.0.0 $ $Date: Jul 31, 2022 16:35:16 $
     */
    public static final class ServerConfigBuilder<T> extends AbstractBuilder<AbstractMailConfigBuilder<T>> {
        /**
         * <span class="en">Is send server configure</span>
         * <span class="zh-CN">是发送服务器配置信息</span>
         */
        private final boolean sendConfig;
        /**
         * <span class="en">Mail server domain name</span>
         * <span class="zh-CN">邮件服务器域名</span>
         */
        private String hostName;
        /**
         * <span class="en">Mail server port</span>
         * <span class="zh-CN">邮件服务器端口号</span>
         */
        private int hostPort = Globals.DEFAULT_VALUE_INT;
        /**
         * <span class="en">Using secure connection to host server</span>
         * <span class="zh-CN">使用安全连接到邮件服务器</span>
         */
        private boolean ssl;
        /**
         * <span class="en">Host server authenticate login</span>
         * <span class="zh-CN">邮件服务器需要身份验证</span>
         */
        private boolean authLogin;
        /**
         * <span class="en">Mail server protocol</span>
         * <span class="zh-CN">邮件服务器协议</span>
         */
        private MailProtocol protocolOption = MailProtocol.UNKNOWN;
        /**
         * <span class="en">Connection timeout(Unit: seconds)</span>
         * <span class="zh-CN">连接超时时间（单位：秒）</span>
         */
        private int connectionTimeout = 5;
        /**
         * <span class="en">Process timeout(Unit: seconds)</span>
         * <span class="zh-CN">操作超时时间（单位：秒）</span>
         */
        private int processTimeout = 5;
        /**
         * <h3 class="en">Private constructor for ServerConfigBuilder</h3>
         * <h3 class="zh-CN">ServerConfigBuilder的私有构造函数</h3>
         *
         * @param parentBuilder     <span class="en">Mail configure builder instance</span>
         *                          <span class="zh-CN">电子邮件配置构造器实例</span>
         * @param sendConfig        <span class="en">Is send server configure</span>
         *                          <span class="zh-CN">是发送服务器配置信息</span>
         * @param serverConfig      <span class="en">Server configure information</span>
         *                          <span class="zh-CN">服务器配置</span>
         */
        private ServerConfigBuilder(final AbstractMailConfigBuilder<T> parentBuilder, final boolean sendConfig,
                                    final MailConfig.ServerConfig serverConfig) {
            super(parentBuilder);
            this.sendConfig = sendConfig;
            if (serverConfig != null) {
                this.hostName = serverConfig.getHostName();
                this.hostPort = serverConfig.getHostPort();
                this.ssl = serverConfig.isSsl();
                this.authLogin = serverConfig.isAuthLogin();
                this.protocolOption = serverConfig.getProtocolOption();
                this.connectionTimeout = serverConfig.getConnectionTimeout();
                this.processTimeout = serverConfig.getProcessTimeout();
            }
        }
        /**
         * <h3 class="en">Configure host server information</h3>
         * <h3 class="zh-CN">设置服务器信息</h3>
         *
         * @param hostAddress   <span class="en">Mail server domain name</span>
         *                      <span class="zh-CN">邮件服务器域名</span>
         * @param hostPort      <span class="en">Mail server port</span>
         *                      <span class="zh-CN">邮件服务器端口号</span>
         *
         * @return  <span class="en">ServerConfigBuilder instance</span>
         *          <span class="zh-CN">邮件服务器配置构建器实例对象</span>
         */
        public ServerConfigBuilder<T> configHost(String hostAddress, int hostPort) {
            this.hostName = hostAddress;
            if (hostPort > 0) {
                this.hostPort = hostPort;
            }
            return this;
        }
        /**
         * <h3 class="en">Configure using secure connection to host server</h3>
         * <h3 class="zh-CN">设置使用安全连接到邮件服务器</h3>
         *
         * @param useSSL    <span class="en">Using secure connection to host server</span>
         *                  <span class="zh-CN">使用安全连接到邮件服务器</span>
         *
         * @return  <span class="en">ServerConfigBuilder instance</span>
         *          <span class="zh-CN">邮件服务器配置构建器实例对象</span>
         */
        public ServerConfigBuilder<T> useSSL(boolean useSSL) {
            this.ssl = useSSL;
            return this;
        }
        /**
         * <h3 class="en">Configure using secure connection to host server</h3>
         * <h3 class="zh-CN">设置使用安全连接到邮件服务器</h3>
         *
         * @param authLogin the auth login
         * <span class="en">Host server authenticate login</span>
         * <span class="zh-CN">邮件服务器需要身份验证</span>
         *
         * @return  <span class="en">ServerConfigBuilder instance</span>
         *          <span class="zh-CN">邮件服务器配置构建器实例对象</span>
         */
        public ServerConfigBuilder<T> authLogin(boolean authLogin) {
            this.authLogin = authLogin;
            return this;
        }
        /**
         * <h3 class="en">Configure mail server protocol</h3>
         * <h3 class="zh-CN">设置邮件服务器协议</h3>
         *
         * @param protocolOption    <span class="en">Mail server protocol</span>
         *                          <span class="zh-CN">邮件服务器协议</span>
         *
         * @return  <span class="en">ServerConfigBuilder instance</span>
         *          <span class="zh-CN">邮件服务器配置构建器实例对象</span>
         */
        public ServerConfigBuilder<T> mailProtocol(final MailProtocol protocolOption) {
            if (!MailProtocol.UNKNOWN.equals(protocolOption)) {
                this.protocolOption = protocolOption;
            }
            return this;
        }
        /**
         * <h3 class="en">Configure connection timeout(Unit: seconds)</h3>
         * <h3 class="zh-CN">设置连接超时时间（单位：秒）</h3>
         *
         * @param connectionTimeout     <span class="en">Connection timeout(Unit: seconds)</span>
         *                              <span class="zh-CN">连接超时时间（单位：秒）</span>
         *
         * @return  <span class="en">ServerConfigBuilder instance</span>
         *          <span class="zh-CN">邮件服务器配置构建器实例对象</span>
         */
        public ServerConfigBuilder<T> connectionTimeout(int connectionTimeout) {
            if (connectionTimeout > 0) {
                this.connectionTimeout = connectionTimeout;
            }
            return this;
        }
        /**
         * <h3 class="en">Configure process timeout(Unit: seconds)</h3>
         * <h3 class="zh-CN">设置操作超时时间（单位：秒）</h3>
         *
         * @param processTimeout <span class="en">Process timeout(Unit: seconds)</span>
         *                       <span class="zh-CN">操作超时时间（单位：秒）</span>
         *
         * @return  <span class="en">ServerConfigBuilder instance</span>
         *          <span class="zh-CN">邮件服务器配置构建器实例对象</span>
         */
        public ServerConfigBuilder<T> processTimeout(int processTimeout) {
            if (processTimeout > 0) {
                this.processTimeout = processTimeout;
            }
            return this;
        }
        /**
         * <h2 class="en">Confirm current configure information</h2>
         * <h2 class="zh-CN">确认当前配置信息</h2>
         *
         * @throws BuilderException the builder exception
         */
        protected void build() throws BuilderException {
            if (StringUtils.isEmpty(this.hostName)) {
                throw new BuilderException(0x0000000E0003L, "Utils", "Host_Address_Unknown_Mail_Error");
            }
            if (MailProtocol.UNKNOWN.equals(this.protocolOption)) {
                throw new BuilderException(0x0000000E0004L, "Utils", "Protocol_Unknown_Mail_Error");
            }
            MailConfig.ServerConfig serverConfig = new MailConfig.ServerConfig();
            serverConfig.setHostName(this.hostName);
            serverConfig.setHostPort(this.hostPort);
            serverConfig.setSsl(this.ssl);
            serverConfig.setAuthLogin(this.authLogin);
            serverConfig.setProtocolOption(this.protocolOption);
            serverConfig.setConnectionTimeout(this.connectionTimeout);
            serverConfig.setProcessTimeout(this.processTimeout);

            super.parentBuilder.serverConfig(this.sendConfig, serverConfig);
        }
    }

}
