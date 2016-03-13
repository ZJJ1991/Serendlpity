package com.example.jzhou.serendlpity;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Bel on 21.02.2016.
 */
public class UserLocalStore {

    // sharedp references file 'userData' for user information where we save all settings about the account of loggedIn user
    public static final String LOCAL_STORE_NAME = "userData";
    SharedPreferences userLocalDatabase;
    User storedUser;

    public UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(LOCAL_STORE_NAME, Context.MODE_PRIVATE);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("username", user.username);
        spEditor.putString("email", user.email);
        spEditor.putString("password", user.password);
        spEditor.apply();
    }

    public User getLoggedInUserData(){
        String username = userLocalDatabase.getString("username", "");
        String email = userLocalDatabase.getString("email", "");
        String password = userLocalDatabase.getString("password","");

        storedUser = new User(username,email,password);
        return storedUser;
    }

    //GET user data separately
    public String getUsername(){
        return userLocalDatabase.getString("username", "");
    }

    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.apply();
    }

    public boolean isUserLoggedIn(){
        return userLocalDatabase.getBoolean("loggedIn", false);
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }

    // set variable to remember user details if he want or don't want to be remembered (depends on the check box)
    public void setRememberUser(boolean rememberUser){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("rememberUser", rememberUser);
        spEditor.apply();
    }

    public boolean isRememberMe(){
        return userLocalDatabase.getBoolean("rememberUser", false);
    }
}
