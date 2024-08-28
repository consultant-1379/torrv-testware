package com.ericsson.nms.rv.taf.test.apache.operators;

/**
 * Created by ejocott on 10/02/2015.
 */
public enum UserRole {
    ADMIN("ADMINISTRATOR"),
    AMOSOPE("Amos_Operator"),
    CMEDITADMIN("Cmedit_Administrator"),
    CMEDITOPE("Cmedit_Operator"),
    FIELDTECH("FIELD_TECHNICIAN"),
    FMADMIN("FM_Administrator"),
    FMOPE("FM_Operator"),
    NETEXTADMIN("Network_Explorer_Administrator"),
    NETEXTOPE("Network_Explorer_Operator"),
    OPERATOR("OPERATOR"),
    SECADMIN("SECURITY_ADMIN"),
    SHMADMIN("Shm_Administrator"),
    SHMOPE("Shm_Operator"),
    TOPBROWADMIN("Topology_Browser_Admin"),
    TOPBROWOPE("Topology_Browser_Operator");

    private final String role;

    private UserRole(String role) {
        this.role = role;
    }

    public String getUserRoleValue() {
        return this.role;
    }

}
