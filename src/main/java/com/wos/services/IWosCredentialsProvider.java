package com.wos.services;

import com.wos.services.model.ISecurityKey;

/**
 * AK/SK Provider
 */
public interface IWosCredentialsProvider {
    public void setSecurityKey(ISecurityKey securityKey);

    public ISecurityKey getSecurityKey();
}
