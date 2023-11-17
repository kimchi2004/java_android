package com.example.javademo.authentication.resetpassword;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javademo.R;

public class CheckEmailActivity extends AppCompatActivity {

    EditText email;
    Button sendinstructionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkemail_to_resetpass);

        email = findViewById(R.id.email);
        sendinstructionsButton = findViewById(R.id.sendinstructionsButton);
        sendinstructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheckEmailValid()) {
                    Toast.makeText(CheckEmailActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CheckEmailActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(CheckEmailActivity.this, "Email invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        sendinstructionsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isCheckEmailValid()){
//
//                }
//            }
//        });
    }

    private boolean isCheckEmailValid() {
        String _email = email.getText().toString();
        boolean isEmailValid = isEmailValid(_email);
        return isEmailValid;
    }

    private boolean isEmailValid(String email) {
        boolean isValid = !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        return isValid;
    }
}
