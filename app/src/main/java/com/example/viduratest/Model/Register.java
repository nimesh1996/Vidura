package com.example.viduratest.Model;

public class Register {

    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String image;
    public boolean isSocialMediaLogging;

    public Register() { }

    public Register(String firstName, String lastName, String email, String password, String image, boolean isSocialMediaLogging) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.image = image;
        this.isSocialMediaLogging = isSocialMediaLogging;
    }

    public boolean isSocialMediaLogging() {
        return isSocialMediaLogging;
    }

    public void setSocialMediaLogging(boolean socialMediaLogging) {
        isSocialMediaLogging = socialMediaLogging;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
