package com.wos.services.model;

/**
 *
 * Rule status
 *
 */
public enum RuleStatusEnum {

    /**
     * Enable a rule.
     */
    ENABLED("Enabled"),

    /**
     * Disable a rule.
     */
    DISABLED("Disabled");

    private String code;

    private RuleStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RuleStatusEnum getValueFromCode(String code) {
        for (RuleStatusEnum val : RuleStatusEnum.values()) {
            if (val.code.equals(code)) {
                return val;
            }
        }
        return null;
    }
}
