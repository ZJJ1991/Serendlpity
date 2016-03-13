package com.example.jzhou.serendlpity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Bel on 18.02.2016.
 */
public class Login extends AppCompatActivity{

    EditText etLogin;
    EditText etPassword;
    ImageButton ibLogin;
    TextView tvForgetPassword;
    TextView tvRegistration;
    TextView tvContinueLoggedOff;
    CheckBox cbRememberMe;

    //user data
    UserLocalStore userLocalStore;

    @Override
    protected void onStart() {
        super.onStart();

        if(userLocalStore.isUserLoggedIn() && userLocalStore.isRememberMe() ){
            displayUserDetails();
            cbRememberMe.setChecked(userLocalStore.isRememberMe());
        }
    }

    private void displayUserDetails(){
        User user = userLocalStore.getLoggedInUserData();

        etLogin.setText(user.email);
        etPassword.setText(user.password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.login);

        //set edittext fields from to variables
        etLogin = (EditText)findViewById(R.id.etLogin);
        etPassword = (EditText)findViewById(R.id.etPassword);

        //set ImageButton and CheckBox to variables
        ibLogin = (ImageButton) findViewById(R.id.ibLogin);
        cbRememberMe = (CheckBox)findViewById(R.id.cbRememberMe);

        //bRegister = (Button) findViewById(R.id.bRegister);
        tvForgetPassword = (TextView)findViewById(R.id.tvForgetPassword);
        tvRegistration = (TextView) findViewById(R.id.tvRegistration);
        tvContinueLoggedOff = (TextView) findViewById(R.id.tvContinueLoggedOff);

        //centralize text in edittexts
        etLogin.setGravity(Gravity.CENTER);
        etPassword.setGravity(Gravity.CENTER);

        //get reference to local store
        userLocalStore = new UserLocalStore(this);

        //set listener to LOGIN button
        ibLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etLogin.getText().toString();
                String password = etPassword.getText().toString();

                User user = new User(email, password);

                authenticate(user);

                userLocalStore.setRememberUser(cbRememberMe.isChecked());
            }
        });

        tvRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showResetPasswordDialog();

                //activity to Reset password

                //Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                //startActivity(intent);
            }
        });

        tvContinueLoggedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapActvity();
                userLocalStore.setUserLoggedIn(false);
            }
        });
    }

    private void authenticate(User user){
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                    goToMapActvity();
                }
            }
        });
    }

    private void logUserIn(User returnedUser){
        //store loggedIn user data in the class file
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);
    }

    private void goToMapActvity(){
        Intent intent = new Intent(getApplicationContext(), Menu.class);
        startActivity(intent);
    }

    private void showErrorMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
        dialogBuilder.setMessage("Incorrect Email/Password Combination");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void showResetPasswordDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Reset Password");

        final EditText input = new EditText(Login.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("Enter your email");
        input.setGravity(Gravity.CENTER);
        alertDialog.setView(input);

        // Setting Icon to Dialog
        alertDialog.setIcon(R.mipmap.ic_key);

        alertDialog.setPositiveButton("Reset password", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                Toast.makeText(getApplicationContext(), "Send password", Toast.LENGTH_SHORT).show();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Close dialog
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

}
