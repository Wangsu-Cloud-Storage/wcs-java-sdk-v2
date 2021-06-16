package com.wos.services.model;

/**
 * Grantee group information in the ACL, {@link AccessControlList}
 */
public class GroupGrantee implements GranteeInterface {

    /**
     * Anonymous user group, indicating all users
     */
    public static final GroupGrantee ALL_USERS = new GroupGrantee(GroupGranteeEnum.ALL_USERS);

    private GroupGranteeEnum groupGranteeType;

    public GroupGrantee() {
    }

    /**
     * Constructor
     * 
     * @param uri
     *            URI for the grantee group
     */
    public GroupGrantee(String uri) {
        this.groupGranteeType = GroupGranteeEnum.getValueFromCode(uri);
    }

    public GroupGrantee(GroupGranteeEnum groupGranteeType) {
        this.groupGranteeType = groupGranteeType;
    }

    /**
     * Set the URI for the grantee group.
     * 
     * @param uri
     *            URI for the grantee group
     */
    @Override
    public void setIdentifier(String uri) {
        this.groupGranteeType = GroupGranteeEnum.getValueFromCode(uri);
    }

    /**
     * Obtain the URI of the grantee group.
     * 
     * @return URI of the grantee group.
     */
    @Override
    public String getIdentifier() {
        return this.groupGranteeType == null ? null : this.groupGranteeType.getCode();
    }

    /**
     * Obtain type of the grantee group.
     * 
     * @return Type of the grantee group
     */
    public GroupGranteeEnum getGroupGranteeType() {
        return this.groupGranteeType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groupGranteeType == null) ? 0 : groupGranteeType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GroupGrantee other = (GroupGrantee) obj;
        if (groupGranteeType != other.groupGranteeType) {
            return false;
        }
        return true;
    }

    /**
     * Return the object description.
     * 
     * @return Object description
     */
    @Override
    public String toString() {
        return "GroupGrantee [" + groupGranteeType + "]";
    }
}
