package com.ericsson.nms.rv.taf.test.apache.operators;

/**
 * Created by ewandaf on 23/07/14.
 */
public enum UserType {
    ENM_USER("enmUser"),
    OTHERS("others");

    private String userType;
    private UserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return this.userType;
    }

    public static UserType getFromString(String v) {
        for (UserType u : UserType.values()) {
            if (u.getUserType().equalsIgnoreCase(v)) {
                return u;
            }
        }
        return null;
    }
}
