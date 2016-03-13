package com.example.jzhou.serendlpity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Bel on 19.02.2016.
 */
public class Register extends AppCompatActivity {

    EditText etUsername;
    EditText etEmail;
    EditText etPassword;
    EditText etPasswordConf;
    TextView tvSignIn;
    ImageButton ibConfirmReg;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.register);

        //find Edit Text fields from the layout and set to variables
        etUsername = (EditText) findViewById(R.id.etUsername);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConf = (EditText) findViewById(R.id.etPasswordConf);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);
        ibConfirmReg = (ImageButton)findViewById(R.id.ibConfirmReg);

        // Centralize text in all Edit Text Fields
        etUsername.setGravity(Gravity.CENTER);
        etEmail.setGravity(Gravity.CENTER);
        etPassword.setGravity(Gravity.CENTER);
        etPasswordConf.setGravity(Gravity.CENTER);

        //get reference to local store
        userLocalStore = new UserLocalStore(this);

        //Confirmation of Registration form for button Sign UP
        ibConfirmReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String passwordConf = etPasswordConf.getText().toString();

                if(isEmailValid(email)){
                    // Check Password and Confirmed Password
                    // if not matched show toast
                    if(password.equals(passwordConf)){
                        User newUser = new User(username, email, password);
                        signUpUser(newUser);
                    }
                    else {
                        showToast("Passwords does not match. Please, try again.");
                    }
                }
                else{
                    showToast("Email is incorrect. Please, try again.");
                }

            }
        });

        //Go to Sign In page From
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });
    }

    //code from http://stackoverflow.com/questions/6119722/how-to-check-edittexts-text-is-email-address-or-not
    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showToast(String errorText){
        Toast toast = Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    private void signUpUser(User user){
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                showToast("Registration successful. Please, Login.");
            }
        });

    }
}
