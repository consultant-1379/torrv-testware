package com.ericsson.nms.rv.taf.test.apache.operators;

/**
 * Created by ewandaf on 23/07/14.
 */
public class User {


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return userName + "[" + firstName + ", " + lastName + ", " + password
                + ", " + email + ", " + status.getUserStatus() + ", "
                + userType.getUserType() + "]";
    }

    public User(String userId, String userName, String firstName, String lastName,
            String password, String email, UserStatus status, UserType userType, UserRole userRole) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.status = status;
        this.userType = userType;
        this.userRole = userRole;
        this._id = userId;
        this._rev = "0";
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.firstName = "defaultFirstName";
        this.lastName = "defaultLastName";
        this.email = "default@ericsson.com";
        this.status = UserStatus.ENABLED;
        this.userType = UserType.ENM_USER;
    }

    private String userName;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private UserStatus status;
    private UserType userType;
    private UserRole userRole;
    private String _rev;
    private String _id;
}
