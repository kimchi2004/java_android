package com.example.javademo.authentication.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javademo.authentication.valid.ValidActivity;
import com.example.javademo.authentication.callback.ILoginCallback;

public class LoginWithUsername {
    private Context context;
    private ILoginCallback callback;
    private LoginActivity loginActivity;

    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_NAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";

    public LoginWithUsername(Context context, ILoginCallback callback){
        this.context = context;
        this.callback = callback;
        this.loginActivity = (LoginActivity) context;
    }

    public void loginWithUsername(){
        final EditText username = loginActivity.getUsername();
        final EditText password = loginActivity.getPassword();
        final Button loginButton = loginActivity.getLoginButton();
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, context.MODE_PRIVATE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameInput = username.getText().toString();
                String passwordInput = password.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_NAME, usernameInput);
                editor.putString(KEY_PASSWORD, passwordInput);
                editor.apply();
                if (ValidActivity.isValidUsername(usernameInput) && ValidActivity.isValidPassword(passwordInput)) {
                    callback.onSuccess(usernameInput);
                    loginActivity.updateLoginName(usernameInput);
                    loginActivity.getDialog().dismiss();
                } else {
                    callback.onFail(" Invalid Username or Password. Please try again!");
                }
            }
        });
    }

    public void logoutUsername() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_NAME);
        editor.remove(KEY_PASSWORD);
        editor.apply();
        Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }
}
