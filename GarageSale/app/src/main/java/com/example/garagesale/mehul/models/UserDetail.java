package com.example.garagesale.mehul.models;

public class UserDetail {
    private String firstName;
    private String lastName;
    private String email;
    private String userUid;
    private String phoneNumber;
    private String address;

    public UserDetail() {}

    public UserDetail(String firstName, String lastName, String email, String userUid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userUid = userUid;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
