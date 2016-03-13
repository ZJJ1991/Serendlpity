package com.example.jzhou.serendlpity;

import java.util.Date;

/**
 * Created by Bel on 21.02.2016.
 */
public class User {

    String username;
    String email;
    String password;
    Date birthday;
    String city;
    String country;

    public User(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthday = null;
        this.city = null;
        this.country = null;
    }

}
