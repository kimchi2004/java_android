package com.example.javademo.authentication.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javademo.R;
import com.example.javademo.authentication.callback.ILoginCallback;
import com.example.javademo.authentication.login.AuthManager;
import com.example.javademo.MainActivity;
import com.example.javademo.authentication.valid.ValidActivity;
import com.example.javademo.model.UserAccountModel;

public class RegisterDialog extends Dialog {
    ILoginCallback callback;
    UserAccountModel userAccountModel;
    Context mContext;
    AlertDialog dialog;
    private MainActivity loginActivity;
    public RegisterDialog(Context context, ILoginCallback iLoginCallback){
        super(context);
        mContext = context;
        callback = iLoginCallback;
        this.loginActivity = (MainActivity) context;
    }
    public void showSignUpDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialog_register = inflater.inflate(R.layout.dialog_register,null);
        builder.setView(dialog_register);
        dialog = builder.create();
        dialog.show();
        final EditText username = (EditText) dialog_register.findViewById(R.id.register_username);
        final EditText password = (EditText) dialog_register.findViewById(R.id.register_password);
        final EditText email = (EditText) dialog_register.findViewById(R.id.register_email);
        final Button signupButton = (Button) dialog_register.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAccountModel = AuthManager.getUserAccountModel();
                String usernameInput = username.getText().toString();
                String passwordInput = password.getText().toString();
                String emailInput = email.getText().toString();
                if (ValidActivity.isValidUsername(usernameInput) && ValidActivity.isValidPassword(passwordInput) && ValidActivity.isEmailValid(emailInput)) {
                    userAccountModel.setUsername(usernameInput);
                    userAccountModel.setPassword(passwordInput);
                    userAccountModel.setEmail(emailInput);
                    AuthManager.getInstance().saveLocal(mContext, userAccountModel);
//                    callback.onSuccess(userAccountModel.getUsername());
                    Toast.makeText(mContext, "Register Successful!", Toast.LENGTH_SHORT).show();
                    loginActivity.updateLoginName(userAccountModel.getUsername());
                    dialog.dismiss();
                } else {
                    callback.onFail("Register failed!");
                }
            }
        });
    }
}
