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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javademo.R;
import com.example.javademo.authentication.callback.ILoginCallback;
import com.example.javademo.authentication.callback.userNameCallback;
import com.example.javademo.authentication.register.SignInDialog;
import com.example.javademo.authentication.resetpassword.CheckEmailDialog;
import com.example.javademo.authentication.resetpassword.ResetPasswordDialog;
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
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements ILoginCallback{
    private GoogleSignInClient gsc;
    GoogleSignInOptions gso;
    CallbackManager callbackManager;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_NAME = "username";
    private static final String EMAIL = "email";
    TextView login_name;
    String loginname = "";
    FirebaseAuth mAuth;
    private ResetPasswordDialog resetPasswordDialog;
    private CheckEmailDialog checkEmailDialog;
    private SignInDialog signInDialog;
    private LoginWithGoogle loginWithGoogle;
    private LoginWithFacebook loginWithFacebook;
    private LoginWithUsername loginWithUsername;
    SignInButton googleBtn;
    public SignInButton getGoogleButton() {
        return googleBtn;
    }
    EditText username;
    EditText password;
    Button loginButton;
    public EditText getUsername(){
        return username;
    }
    public EditText getPassword(){
        return password;
    }
    public Button getLoginButton(){
        return loginButton;
    }

    public void onSuccess(String username) {
        if (username != null && !username.isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Login Successful! " + username, Toast.LENGTH_SHORT).show();
        }
    }

    public void onFail(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    //onCreate
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        login_name = findViewById(R.id.login_name);

        resetPasswordDialog = new ResetPasswordDialog(this);
        checkEmailDialog = new CheckEmailDialog(this);
        signInDialog = new SignInDialog(this, this);
        loginWithGoogle = new LoginWithGoogle(this,this);
        loginWithFacebook = new LoginWithFacebook(this,this);
        loginWithUsername = new LoginWithUsername(this,this);

        //username
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loginname = sharedPreferences.getString(KEY_NAME, "");
        updateLoginName(loginname);

        //google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            login_name.setText(personName);
        }

        //facebook
        loginWithFacebook.fetchFacebookUserName(new userNameCallback() {
            public void onFullNameReceived(String fullName) {
                login_name.setText(fullName);
            }
        });
    }

    //onActivityResult------------------
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            loginWithGoogle.handleGoogleSignInResult(data);
        }
    }

    //username
    public void updateLoginName(String loginname) {
        login_name.setText(loginname);
    }

    public void dialog_loginShow(View view) {
        //logout
        loginWithUsername.logoutUsername();
        mAuth.signOut();
        loginWithGoogle.googleSignOut();
        LoginManager.getInstance().logOut();

        //create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialog_login = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(dialog_login);
        AlertDialog dialog = builder.create();
        dialog.show();

        //login with username---------------------
        username = (EditText) dialog_login.findViewById(R.id.username);
        password = (EditText) dialog_login.findViewById(R.id.password);
        loginButton = (Button) dialog_login.findViewById(R.id.loginButton);
        loginWithUsername.loginWithUsername();

        //login with facebook-----------------
        final LoginButton facebookBtn = (LoginButton) dialog_login.findViewById(R.id.facebook_button);
        facebookBtn.setReadPermissions(Arrays.asList(EMAIL));
        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
            }
        });

        facebookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("letSee", "Facebook Token: " + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                Log.d("letSee", "Facebook onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("letSee", "Facebook onError ");
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loginWithFacebook.fetchFacebookUserName(new userNameCallback() {
                            public void onFullNameReceived(String fullName) {
                                LoginActivity.this.onSuccess(fullName);
                            }
                        });
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("letSee", "Facebook onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("letSee", "Facebook onError ");
                    }
                });

        //login with google
        googleBtn = dialog_login.findViewById(R.id.google_button);
        loginWithGoogle.loginWithGoogle();
    }

    //sign up--------------------
    public void dialog_signupshow(View view) {
        signInDialog.showSignInDialog();
    }

    //forgot password-------------------
    public void dialog_forgotpwShow(View view) {
        checkEmailDialog.showCheckEmailDialog();
    }

    public void dialog_resetpwShow(View view) {
        resetPasswordDialog.showResetPasswordDialog();
    }
}
