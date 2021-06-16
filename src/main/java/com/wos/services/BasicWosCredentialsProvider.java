package com.wos.services;

import com.wos.services.internal.security.BasicSecurityKey;
import com.wos.services.model.ISecurityKey;

public class BasicWosCredentialsProvider implements IWosCredentialsProvider {

    private volatile ISecurityKey securityKey;

    public BasicWosCredentialsProvider(ISecurityKey securityKey) {
        setSecurityKey(securityKey);
    }

    public BasicWosCredentialsProvider(String accessKey, String secretKey) {
        this(accessKey, secretKey, null);
    }

    public BasicWosCredentialsProvider(String accessKey, String secretKey, String securityToken) {
        checkSecurityKey(accessKey, secretKey);
        setSecurityKey(new BasicSecurityKey(accessKey, secretKey, securityToken));
    }

    private static void checkSecurityKey(String accessKey, String secretKey) {
        if (accessKey == null) {
            throw new IllegalArgumentException("accessKey should not be null.");
        }

        if (secretKey == null) {
            throw new IllegalArgumentException("secretKey should not be null.");
        }
    }

    @Override
    public void setSecurityKey(ISecurityKey securityKey) {
        if (securityKey == null) {
            throw new IllegalArgumentException("securityKey should not be null.");
        }
        checkSecurityKey(securityKey.getAccessKey(), securityKey.getSecretKey());

        this.securityKey = securityKey;
    }

    @Override
    public ISecurityKey getSecurityKey() {
        if (this.securityKey == null) {
            throw new IllegalArgumentException("Invalid securityKey");
        }

        return this.securityKey;
    }
}
