package com.example.javademo.authentication.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javademo.R;
import com.example.javademo.ValidActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInClient gsc;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }

    //onActivityResult------------------
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), LoginDetailActivity.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }

    }

    //onStart--------------
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(this, LoginDetailActivity.class);
            startActivity(intent);
        }
    }

    //login---------------------
        SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_NAME = "username";
    private static final String KEY_PASSWORD = "password";

    public void dialog_loginShow(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialog_login = inflater.inflate(R.layout.dialog_login,null);
        builder.setView(dialog_login);
        AlertDialog dialog = builder.create();
        dialog.show();

        //login with username---------------------
        final EditText username = (EditText) dialog_login.findViewById(R.id.username);
        final EditText password = (EditText) dialog_login.findViewById(R.id.password);
        final Button loginButton = (Button) dialog_login.findViewById(R.id.loginButton);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        String name = sharedPreferences.getString(KEY_NAME, null);
//        if (name!=null) {
//            Intent intent = new Intent(LoginActivity.this,LoginDetailActivity.class);
//            startActivity(intent);
//        }
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
//                    Intent intent = new Intent(LoginActivity.this, LoginDetailActivity.class);
//                    intent.putExtra("USERNAME_KEY", usernameInput);
//                    startActivity(intent);
//                    finish();
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this, usernameInput, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //sign up Button in login dialog-------------
        final Button signupButton = (Button) dialog_login.findViewById(R.id.dialog_sigupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_signupshow(v);
            }
        });

        //login with facebook-----------------
        final LoginButton facebookBtn = (LoginButton) dialog_login.findViewById(R.id.facebook_button);
        facebookBtn.setReadPermissions(Arrays.asList(EMAIL));
        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        if(accessToken!=null && accessToken.isExpired()== false){
//            startActivity(new Intent(LoginActivity.this, LoginWithFacebookdetail.class));
//            finish();
//        }
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
            }
        });

        facebookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("letSee","Facebook Token: " + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                Log.d("letSee","Facebook onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("letSee","Facebook onError ");
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
//                        startActivity(new Intent(LoginActivity.this, LoginWithFacebookdetail.class));
//                        finish();
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(LoginActivity.this,"", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancel() {
                        Log.d("letSee","Facebook onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("letSee","Facebook onError ");
                    }
                });


        //login with google---------------
        final SignInButton googleBtn = (SignInButton) dialog_login.findViewById(R.id.google_button);
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, options);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = gsc.getSignInIntent();
                startActivityForResult(i, 1234);

            }
        });
    }

    //sign up--------------------
    public void dialog_signupshow(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialog_register = inflater.inflate(R.layout.dialog_register,null);
        builder.setView(dialog_register);
        AlertDialog dialog = builder.create();
        dialog.show();

        final EditText username = (EditText) dialog_register.findViewById(R.id.register_username);
        final EditText password = (EditText) dialog_register.findViewById(R.id.register_password);
        final EditText email = (EditText) dialog_register.findViewById(R.id.register_email);
        final Button signupButton = (Button) dialog_register.findViewById(R.id.signupButton);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameInput = username.getText().toString();
                String passwordInput = password.getText().toString();
                String emailInput = email.getText().toString();
                if (ValidActivity.isValidUsername(usernameInput) && ValidActivity.isValidPassword(passwordInput) && ValidActivity.isEmailValid(emailInput)) {
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this, "Register Successful!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(LoginActivity.this, "Register failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //forgot password-------------------
    public void dialog_forgotpwShow(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
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
                    Toast.makeText(LoginActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                    dialog_resetpwShow(v);
                } else{
                    Toast.makeText(LoginActivity.this, "Email invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void dialog_resetpwShow(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialog_resetpw = inflater.inflate(R.layout.dialog_forgotpassword,null);
        builder.setView(dialog_resetpw);
        AlertDialog dialog = builder.create();
        dialog.show();

        final EditText newpw = (EditText) dialog_resetpw.findViewById(R.id.newpassword);
        final EditText confirmnewpw = (EditText) dialog_resetpw.findViewById(R.id.confirmnewpassword);
        final Button resetpwButton = (Button) dialog_resetpw.findViewById(R.id.resetpasswordButton);
        resetpwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newpwInput = newpw.getText().toString();
                String confirmnewpwInput = confirmnewpw.getText().toString();
                if (ValidActivity.isResetPasswordValid(newpwInput,confirmnewpwInput)) {
                    dialog_loginShow(v);
                    Toast.makeText(LoginActivity.this, "Change Password Successful!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "Change Password failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
