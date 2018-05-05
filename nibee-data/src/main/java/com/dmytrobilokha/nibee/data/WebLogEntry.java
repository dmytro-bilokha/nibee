package com.dmytrobilokha.nibee.data;

import java.time.LocalDateTime;

public class WebLogEntry {

    private Long id;
    private String uuid;
    private String sessionId;
    private int serverPort;
    private String requestUri;
    private String queryString;
    private String referer;
    private String userAgent;
    private String acceptEncoding;
    private LocalDateTime createdOn;
    private int clientPort;
    private String clientIp;

    WebLogEntry() {
        //The constructor required by MyBatis
    }

    private WebLogEntry(Builder builder) {
        this.uuid = builder.uuid;
        this.sessionId = builder.sessionId;
        this.serverPort = builder.serverPort;
        this.requestUri = builder.requestUri;
        this.queryString = builder.queryString;
        this.referer = builder.referer;
        this.userAgent = builder.userAgent;
        this.acceptEncoding = builder.acceptEncoding;
        this.clientPort = builder.clientPort;
        this.clientIp = builder.clientIp;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "WebLogEntry{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", serverPort=" + serverPort +
                ", requestUri='" + requestUri + '\'' +
                ", queryString='" + queryString + '\'' +
                ", referer='" + referer + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", acceptEncoding='" + acceptEncoding + '\'' +
                ", createdOn=" + createdOn +
                ", clientPort=" + clientPort +
                ", clientIp='" + clientIp + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getReferer() {
        return referer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public int getClientPort() {
        return clientPort;
    }

    public String getClientIp() {
        return clientIp;
    }

    public static class Builder {

        private String uuid;
        private String sessionId;
        private Integer serverPort;
        private String requestUri;
        private String queryString;
        private String referer;
        private String userAgent;
        private String acceptEncoding;
        private Integer clientPort;
        private String clientIp;

        private Builder() {
            //Not going to instantiate builder outside
        }

        public WebLogEntry build() {
            validate();
            return new WebLogEntry(this);
        }

        private void validate() {
            boolean invalid = uuid == null || sessionId == null || serverPort == null || requestUri == null
                    || clientPort == null || clientIp == null;
            if (invalid) {
                throw new IllegalStateException("Unable to create WebLogEntry from invalid " + this);
            }
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "uuid='" + uuid + '\'' +
                    ", sessionId='" + sessionId + '\'' +
                    ", serverPort=" + serverPort +
                    ", requestUri='" + requestUri + '\'' +
                    ", queryString='" + queryString + '\'' +
                    ", referer='" + referer + '\'' +
                    ", userAgent='" + userAgent + '\'' +
                    ", acceptEncoding='" + acceptEncoding + '\'' +
                    ", clientPort=" + clientPort +
                    ", clientIp='" + clientIp + '\'' +
                    '}';
        }

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder serverPort(int port) {
            this.serverPort = port;
            return this;
        }

        public Builder requestUri(String requestUri) {
            this.requestUri = requestUri;
            return this;
        }

        public Builder queryString(String queryString) {
            this.queryString = queryString;
            return this;
        }

        public Builder referer(String referer) {
            this.referer = referer;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder acceptEncoding(String acceptEncoding) {
            this.acceptEncoding = acceptEncoding;
            return this;
        }

        public Builder clientPort(int port) {
            this.clientPort = port;
            return this;
        }

        public Builder clientIp(String ip) {
            this.clientIp = ip;
            return this;
        }
    }
}
