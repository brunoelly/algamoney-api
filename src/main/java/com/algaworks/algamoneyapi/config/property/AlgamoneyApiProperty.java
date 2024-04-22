package com.algaworks.algamoneyapi.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties("algamoney")
@Component
public class AlgamoneyApiProperty {

    private String allowedOrigin = "http://localhost:8000";

    private final Security security = new Security();

    private final S3 s3 = new S3();

    public S3 getS3() {
        return s3;
    }

    private final Mail mail = new Mail();

    public Mail getMail() {
        return mail;
    }

    public Security getSecurity() {
        return security;
    }

    public String getAllowedOrigin() {
        return allowedOrigin;
    }

    public void setAllowedOrigin(String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    public static class S3 {

        private String accessKeyId;

        private String secretAccessKey;

        private String bucket = "aw-algamoney-arquivos";

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getSecretAccessKey() {
            return secretAccessKey;
        }

        public void setSecretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
        }
    }

    public static class Security {

        private List<String> allowedRedirects;
        private String authServerUrl;

        public List<String> getAllowedRedirects() {
            return allowedRedirects;
        }

        public void setAllowedRedirects(List<String> allowedRedirects) {
            this.allowedRedirects = allowedRedirects;
        }

        public String getAuthServerUrl() {
            return authServerUrl;
        }

        public void setAuthServerUrl(String authServerUrl) {
            this.authServerUrl = authServerUrl;
        }
    }

    public static class Mail {

        private String host;

        private Integer port;

        private String username;

        private String password;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}