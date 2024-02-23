package com.hayk.healthmanagerregistration;

public class User {
    String userName;
    String email;
    boolean emailVerified;
    public User(String userName, String email, boolean emailVerified){
        this.userName = userName;
        this.email = email;
        this.emailVerified = emailVerified;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
