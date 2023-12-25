package com.example.javademo.authentication.register;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.javademo.R;
import com.example.javademo.authentication.valid.ValidActivity;
import com.example.javademo.authentication.callback.ILoginCallback;
import com.example.javademo.authentication.login.LoginActivity;

public class SignInDialog {
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_NAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private Context context;
    private ILoginCallback callback;
    private LoginActivity loginActivity;
    public SignInDialog(Context context, ILoginCallback callback){
        this.context = context;
        this.callback = callback;
        this.loginActivity = (LoginActivity) context;
    }
    public void showSignInDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialog_register = inflater.inflate(R.layout.dialog_register,null);
        builder.setView(dialog_register);
        AlertDialog dialog = builder.create();
        dialog.show();

        final EditText username = (EditText) dialog_register.findViewById(R.id.register_username);
        final EditText password = (EditText) dialog_register.findViewById(R.id.register_password);
        final EditText email = (EditText) dialog_register.findViewById(R.id.register_email);
        final Button signupButton = (Button) dialog_register.findViewById(R.id.signupButton);

        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,context.MODE_PRIVATE);
        String name = sharedPreferences.getString(KEY_NAME, null);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameInput = username.getText().toString();
                String passwordInput = password.getText().toString();
                String emailInput = email.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_NAME, usernameInput);
                editor.putString(KEY_PASSWORD, passwordInput);
                editor.putString(KEY_EMAIL, emailInput);
                editor.apply();
                if (ValidActivity.isValidUsername(usernameInput) && ValidActivity.isValidPassword(passwordInput) && ValidActivity.isEmailValid(emailInput)) {
                    callback.onSuccess(usernameInput);
                    dialog.dismiss();
                    loginActivity.getDialog().dismiss();
                    loginActivity.updateLoginName(name);
                } else {
                    callback.onFail("Register failed!");
                }
            }
        });
    }
}
