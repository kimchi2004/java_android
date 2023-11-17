package com.example.javademo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email;
    EditText newpassword;
    EditText confirm_newpassword;
    Button resetpasswordButton;
    Button loginnowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        newpassword = findViewById(R.id.newpassword);
        confirm_newpassword = findViewById(R.id.confirmnewpassword);
        resetpasswordButton = findViewById(R.id.resetpasswordButton);
        loginnowButton = findViewById(R.id.loginnowButton);
        resetpasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isResetPasswordValid()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Change Password Successful!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ForgotPasswordActivity.this, "Change Password failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginnowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isResetPasswordValid() {
        String _newpassword = newpassword.getText().toString();
        String _confirm_newpassword = confirm_newpassword.getText().toString();

        boolean isPasswordValid = isValidPassword(_newpassword);

        return !_newpassword.isEmpty() && !_confirm_newpassword.isEmpty() && _confirm_newpassword.equals(_newpassword) && isPasswordValid;
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-zA-Z0-9])(?=.*[@#$%^&+=]).{6,}$";
        return password.matches(passwordRegex);
    }

}
