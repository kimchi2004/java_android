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
import com.example.javademo.authentication.valid.ValidActivity;


public class ForgotPasswordDialog extends Dialog {
    ILoginCallback callback;
    Context mContext;
    AlertDialog dialog;
    private LoginDialog loginDialog;
    public ForgotPasswordDialog(Context context, ILoginCallback iLoginCallback){
        super(context);
        mContext = context;
        callback = iLoginCallback;
    }
    public void showCheckEmailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialog_forgotpw = inflater.inflate(R.layout.checkemail_to_resetpass,null);
        builder.setView(dialog_forgotpw);
        AlertDialog dialog = builder.create();
        dialog.show();

        final EditText email = (EditText) dialog_forgotpw.findViewById(R.id.email);
        final Button sendinsButton = (Button) dialog_forgotpw.findViewById(R.id.sendinstructionsButton);
        sendinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = email.getText().toString();
                if (ValidActivity.isEmailValid(emailInput)) {
                    Toast.makeText(mContext, "Successful!", Toast.LENGTH_SHORT).show();
                    showResetPasswordDialog();
                    dialog.dismiss();
                } else{
                    Toast.makeText(mContext, "Email invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialog_resetpw = inflater.inflate(R.layout.dialog_forgotpassword, null);
        builder.setView(dialog_resetpw);
        AlertDialog dialog = builder.create();
        dialog.show();

        final EditText newpw = dialog_resetpw.findViewById(R.id.newpassword);
        final EditText confirmnewpw = dialog_resetpw.findViewById(R.id.confirmnewpassword);
        final Button resetpwButton = dialog_resetpw.findViewById(R.id.resetpasswordButton);
        resetpwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newpwInput = newpw.getText().toString();
                String confirmnewpwInput = confirmnewpw.getText().toString();
                if (ValidActivity.isResetPasswordValid(newpwInput, confirmnewpwInput)) {
                    Toast.makeText(mContext, "Change Password Successful!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(mContext, "Change Password failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
