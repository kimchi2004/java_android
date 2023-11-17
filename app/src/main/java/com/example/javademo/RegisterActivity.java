package com.example.javademo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText email;
    Button signupButton;
    Button loginnowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        username = findViewById(R.id.register_username);
        password = findViewById(R.id.register_password);
        email = findViewById(R.id.register_email);
        signupButton = findViewById(R.id.signupButton);
        loginnowButton = findViewById(R.id.loginnowButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRegistrationValid()) {
                    Toast.makeText(RegisterActivity.this, "Register Successful!", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(RegisterActivity.this, "Register failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginnowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isRegistrationValid() {
        String _username = username.getText().toString();
        String _password = password.getText().toString();
        String _email = email.getText().toString();

        boolean isUsernameValid = isUsernameValid(_username);
        boolean isPasswordValid = isPasswordValid(_password);
        boolean isEmailValid = isEmailValid(_email);

        return isUsernameValid && isPasswordValid && isEmailValid;
    }

    private boolean isUsernameValid(String username) {
        String usernameRegex = "^[a-zA-Z0-9_]{6,30}$";
        boolean isValid = username.matches(usernameRegex);
        return isValid;
    }

    private boolean isPasswordValid(String password) {
        String passwordRegex = "^(?=.*[a-zA-Z0-9])(?=.*[@#$%^&+=]).{6,}$";
        boolean isValid = password.matches(passwordRegex);
        return isValid;
    }

    private boolean isEmailValid(String email) {
        boolean isValid = !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        return isValid;
    }
}
