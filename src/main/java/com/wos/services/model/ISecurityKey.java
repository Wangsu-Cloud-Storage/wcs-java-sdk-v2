package com.wos.services.model;

public interface ISecurityKey {
    /**
     * @return Returns the access key for this securityKey.
     */
    public String getAccessKey();

    /**
     * @return Returns the secret key for this securityKey.
     */
    public String getSecretKey();

    /**
     * @return Returns the security token for this securityKey.
     */
    public String getSecurityToken();
}
