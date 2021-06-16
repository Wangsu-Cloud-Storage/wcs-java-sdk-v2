package com.wos.services.internal.security;

import com.wos.services.model.ISecurityKey;

public class BasicSecurityKey implements ISecurityKey {
    protected String accessKey;
    protected String secretKey;
    protected String securityToken;

    public BasicSecurityKey(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public BasicSecurityKey(String accessKey, String secretKey, String securityToken) {
        this(accessKey, secretKey);
        this.securityToken = securityToken;
    }

    @Override
    public String getAccessKey() {
        return accessKey;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public String getSecurityToken() {
        return securityToken;
    }
}
