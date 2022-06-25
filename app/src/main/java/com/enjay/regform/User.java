package com.enjay.regform;

import android.graphics.Bitmap;

import java.util.List;

public class User {
    String username;
    String fullName;
    String email;
    String number;
    String password;
    String gender;
    String hobbies;
    Bitmap img;

    public User(
            String username,
            String fullName,
            String email,
            String number,
            String password,
            String gender,
            String hobbies,
            Bitmap img
    ) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.number = number;
        this.password = password;
        this.gender = gender;
        this.hobbies = hobbies;
        this.img = img;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }

    public String getPassword() {
        return password;
    }


    public String getGender() {
        return gender;
    }

    public String getHobbies() {
        return hobbies;
    }

    public Bitmap getImg() {
        return img;
    }

    @Override
    public String toString() {
        return "User { " +
                "\nusername='" + username + '\'' +
                ",\n fullName='" + fullName + '\'' +
                ",\n email='" + email + '\'' +
                ",\n number='" + number + '\'' +
                ",\n password='" + password + '\'' +
                ",\n gender='" + gender + '\'' +
                ",\n hobbies='" + hobbies + '\'' +
                "\n"+'}';
    }
}
