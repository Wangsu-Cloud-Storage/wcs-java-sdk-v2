package com.wos.services.model;

/**
 * Interface abstraction of the grantee or grantee group in the ACL
 * {@link AccessControlList}
 */
public interface GranteeInterface {
    /**
     * Set the identifier marking the grantee (group).
     * 
     * @param id
     *            Identifier marking the grantee (group)
     */
    public void setIdentifier(String id);

    /**
     * Obtain the identifier marking the grantee (group).
     * 
     * @return Identifier marking the grantee (group)
     */
    public String getIdentifier();

}
