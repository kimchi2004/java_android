package com.example.javademo.authentication.resetpassword;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javademo.R;
import com.example.javademo.authentication.valid.ValidActivity;
import com.example.javademo.authentication.login.LoginActivity;

public class CheckEmailDialog {
    private Context context;
    private LoginActivity loginActivity;
    public CheckEmailDialog(Context context){
        this.context = context;
        this.loginActivity = (LoginActivity) context;
    }
    public void showCheckEmailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
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
                    Toast.makeText(context, "Successful!", Toast.LENGTH_SHORT).show();
                    loginActivity.dialog_resetpwShow(v);
                    dialog.dismiss();
                } else{
                    Toast.makeText(context, "Email invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
