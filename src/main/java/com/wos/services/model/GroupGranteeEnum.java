package com.wos.services.model;

import com.wos.services.internal.Constants;

/**
 * Type of the user group
 *
 */
public enum GroupGranteeEnum {
    /**
     * Anonymous user group, indicating all users
     */
    ALL_USERS;


    public String getCode() {
        return this.name();
    }

    public static GroupGranteeEnum getValueFromCode(String code) {
        if ("Everyone".equals(code) || Constants.ALL_USERS_URI.equals(code)) {
            return ALL_USERS;
        }
        return null;
    }
}
