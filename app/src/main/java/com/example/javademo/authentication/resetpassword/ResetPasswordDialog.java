package com.example.javademo.authentication.resetpassword;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javademo.R;
import com.example.javademo.authentication.ValidActivity;

public class ResetPasswordDialog {
    private Context context;

    public ResetPasswordDialog(Context context) {
        this.context = context;
    }

    public void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
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
                    Toast.makeText(context, "Change Password Successful!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Change Password failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
