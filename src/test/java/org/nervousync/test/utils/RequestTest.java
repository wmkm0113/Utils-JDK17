package org.nervousync.test.utils;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.beans.servlet.request.RequestInfo;
import org.nervousync.beans.servlet.response.ResponseInfo;
import org.nervousync.enumerations.web.HttpMethodOption;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.RequestUtils;
import org.nervousync.utils.StringUtils;

import java.security.cert.CertificateEncodingException;
import java.util.Optional;

public final class RequestTest extends BaseTest {

    @Test
    @Order(0)
    public void httpMethod() {
        this.logger.info("Request_HTTP_Method", RequestUtils.httpMethodOption("get"));
        this.logger.info("Request_HTTP_Method", RequestUtils.httpMethodOption("post"));
        this.logger.info("Request_HTTP_Method", RequestUtils.httpMethodOption("put"));
        this.logger.info("Request_HTTP_Method", RequestUtils.httpMethodOption("trace"));
        this.logger.info("Request_HTTP_Method", RequestUtils.httpMethodOption("head"));
        this.logger.info("Request_HTTP_Method", RequestUtils.httpMethodOption("delete"));
        this.logger.info("Request_HTTP_Method", RequestUtils.httpMethodOption("options"));
    }

    @Test
    @Order(10)
    public void DNS() {
        this.logger.info("Request_Resolve_Domain", "www.baidu.com", RequestUtils.resolveDomain("www.baidu.com"));
    }

    @Test
    @Order(20)
    public void SSL() {
        Optional.ofNullable(RequestUtils.serverCertificate("https://www.baidu.com"))
                .ifPresent(certificate -> {
                    try {
                        this.logger.info("Request_Read_Certificate", StringUtils.base64Encode(certificate.getEncoded()));
                    } catch (CertificateEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    @Order(30)
    public void contentLength() {
        this.logger.info("Request_Content_Length", RequestUtils.contentLength("http://www.baidu.com"));
    }

    @Test
    @Order(40)
    public void sendRequest() {
        RequestInfo requestInfo = RequestInfo.builder(HttpMethodOption.GET).requestUrl("http://www.baidu.com").build();
        Optional.ofNullable(RequestUtils.sendRequest(requestInfo, ResponseInfo.class))
                .ifPresent(responseInfo ->
                        this.logger.info("Request_Content_Info",
                                responseInfo.getContentLength(), responseInfo.parseString()));
    }
}
