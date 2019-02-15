package com.example.fallennymous.prekjutelulas.Model;

/**
 * Created by fallennymous on 27/05/2018.
 */

public class User {
    private String Name;
    private String password;
    private String Phone;
    private String IsStaff;

    public User() {
    }

    public User(String name, String password) {
        Name = name;
        this.password = password;
        IsStaff="false";
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
