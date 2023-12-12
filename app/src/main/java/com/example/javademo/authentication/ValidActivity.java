package com.example.javademo.authentication;

import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

public class ValidActivity extends AppCompatActivity {
    //check valid
    public static boolean isValidUsername(String username) {
        String usernameRegex = "^[a-zA-Z0-9_]{6,30}$";
        return username.matches(usernameRegex);
    }

    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-zA-Z0-9])(?=.*[@#$%^&+=]).{6,}$";
        return password.matches(passwordRegex);
    }
    public static boolean isEmailValid(String email) {
        boolean isValid = !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        return isValid;
    }
    public static boolean isResetPasswordValid(String newpassword, String confirm_newpassword) {
        boolean isPasswordValid = isValidPassword(newpassword);
        return !newpassword.isEmpty() && !confirm_newpassword.isEmpty() && confirm_newpassword.equals(newpassword) && isPasswordValid;
    }
}
