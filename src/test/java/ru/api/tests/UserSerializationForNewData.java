package ru.api.tests;

public class UserSerializationForNewData {
    private String newName;
    private String newEmail;
    private String newPassword;

    public UserSerializationForNewData(){}

    public UserSerializationForNewData(String newName, String newEmail, String newPassword){
        this.newName = newName;
        this.newEmail = newEmail;
        this.newPassword = newPassword;
    }

    public UserSerializationForNewData(String newPassword){
        this.newPassword = newPassword;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
