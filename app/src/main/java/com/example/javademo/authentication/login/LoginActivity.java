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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javademo.R;
import com.example.javademo.authentication.ILoginCallback;
import com.example.javademo.authentication.UserObject;
import com.example.javademo.authentication.ValidActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements ILoginCallback{
    private GoogleSignInClient gsc;
    GoogleSignInOptions gso;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    TextView login_name;
    String loginname = "";

    String fullName = "";
    FirebaseAuth mAuth;

    final UserObject userCallback = new UserObject();

    //callback
    public void onSuccess(String username) {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
        login_name.setText(userCallback.getFullName());
        Toast.makeText(this, "Login Successful! " + userCallback.getFullName(), Toast.LENGTH_SHORT).show();
    }
    public void onFail(String errorMessage) {
        Toast.makeText(this,errorMessage, Toast.LENGTH_SHORT).show();
    }

    //onCreate
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        login_name = findViewById(R.id.login_name);

        //username
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loginname = sharedPreferences.getString(KEY_NAME, "");
//        updateLoginName();

        //google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
//            login_name.setText(personName);
        }

        //facebook
        fetchFacebookUserName();
    }

    //onActivityResult------------------
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
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
                                    onSuccess(acct.getDisplayName());
                                } else {
                                    onFail(task.getException().getMessage());
                                }

                            }
                        });

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }

    }

    //googleSignOut
    private void googleSignOut() {
        gsc.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    //facebook

    private void fetchFacebookUserName() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                if (object != null && object.has("name")) {
                                    String fullName = object.getString("name");
                                    userCallback.create(fullName);
//                                    login_name.setText(fullName);
                                    onSuccess(fullName);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "name");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }


    //login---------------------
        SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_NAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";

    //username
    private void updateLoginName() {
        login_name.setText(loginname);
    }

    public void logoutUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_NAME);
        editor.remove(KEY_PASSWORD);
        editor.apply();

        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }

    public void dialog_loginShow(View view) {
        logoutUsername();
        mAuth.signOut();
        googleSignOut();
        LoginManager.getInstance().logOut();
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
                    onSuccess(usernameInput);
                    loginname = usernameInput;
                    dialog.dismiss();
                    updateLoginName();
                } else {
                    onFail(" Invalid Username or Password. Please try again!");
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
                        fetchFacebookUserName();
//                        LoginActivity.this.onSuccess(userCallback.getFullName());
                        dialog.dismiss();
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
                dialog.dismiss();
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

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        String name = sharedPreferences.getString(KEY_NAME, null);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameInput = username.getText().toString();
                String passwordInput = password.getText().toString();
                String emailInput = email.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_NAME, usernameInput);
                editor.putString(KEY_PASSWORD, passwordInput);
                editor.putString(KEY_EMAIL, emailInput);
                editor.apply();
                if (ValidActivity.isValidUsername(usernameInput) && ValidActivity.isValidPassword(passwordInput) && ValidActivity.isEmailValid(emailInput)) {
//                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();
                    onSuccess(usernameInput);
                    loginname = usernameInput;
                    dialog.dismiss();
                    updateLoginName();
                } else {
                    onFail("Register failed!");
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
