package com.ericsson.nms.rv.taf.test.apache.operators;

/**
 * Created by ewandaf on 23/07/14.
 */
public enum UserStatus {
    ENABLED("enabled"),
    DISABLED("disabled");

    private String userType;
    private UserStatus(String userType) {
        this.userType = userType;
    }

    public String getUserStatus() {
        return this.userType;
    }

    public static UserStatus getFromString(String v) {
        for (UserStatus u : UserStatus.values()) {
            if (u.getUserStatus().equalsIgnoreCase(v)) {
                return u;
            }
        }
        return null;
    }
}
