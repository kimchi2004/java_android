package com.example.javademo.authentication.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

import com.example.javademo.MainActivity;
import com.example.javademo.R;
import com.example.javademo.authentication.callback.ILoginCallback;
import com.example.javademo.authentication.callback.userNameCallback;
import com.example.javademo.authentication.login.AuthManager;
import com.example.javademo.model.UserAccountModel;
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

public class LoginDialog extends Dialog {
    ILoginCallback callback;
    UserAccountModel userAccountModel;
    Context mContext;
    AlertDialog dialog;
    Button loginButton;
    EditText username;
    EditText password;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_NAME = "username";
    private static final String KEY_PASSWORD = "password";
    private MainActivity loginActivity;
    private GoogleSignInClient gsc;
    SignInButton googleBtn;
    LoginButton facebookBtn;
    private boolean isNameUpdated = false;
    private static final String EMAIL = "email";
    Button dialog_forgotpasswordButton;
    Button dialog_sigupButton;
    RegisterDialog registerDialog;
    ForgotPasswordDialog forgotPasswordDialog;
    public LoginDialog(@NonNull Context context, ILoginCallback iLoginCallback) {
        super(context);
        mContext = context;
        callback = iLoginCallback;
        this.loginActivity = (MainActivity) context;
        initializeGoogleSignInClient();
    }
    //login with username

    public void loginWithUsername(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAccountModel = AuthManager.getUserAccountModel();
                String usernameInput = username.getText().toString();
                String passwordInput = password.getText().toString();
                if (usernameInput.equals(userAccountModel.getUsername()) &&
                        passwordInput.equals(userAccountModel.getPassword())) {
                    userAccountModel.setUsername(usernameInput);
                    userAccountModel.setPassword(passwordInput);
                    AuthManager.getInstance().saveLocal(mContext, userAccountModel);
                    callback.onSuccess(userAccountModel.getUsername());
                    dialog.dismiss();
                } else {
                    callback.onFail(" Invalid Username or Password. Please try again!");
                }
            }
        });
    }

    public void logoutUsername() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_NAME);
        editor.remove(KEY_PASSWORD);
        editor.apply();
        Toast.makeText(mContext, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }

    //login with google
    private void initializeGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(mContext, gso);
    }

    public void loginWithGoogle(){
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = gsc.getSignInIntent();
                ((Activity) mContext).startActivityForResult(i, 1234);
                dialog.dismiss();
            }
        });
    }

    public void googleSignOut() {
        gsc.signOut().addOnCompleteListener((Activity) mContext, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    public void handleGoogleSignInResult(Intent data){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(mContext);
        if (acct != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(callback!=null){
                                    if (task.isSuccessful()) {
                                        callback.onSuccess(acct.getDisplayName());
                                    } else {
                                        callback.onFail(task.getException().getMessage());
                                    }
                                }
                            }
                        });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    //login with facebook
    public void fetchFacebookUserName(userNameCallback ucallback) {
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
                                    if (!isNameUpdated) {
                                        ucallback.onFullNameReceived(fullName);
                                        isNameUpdated = true;
                                    }
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
    public void loginWithFacebook(){
        facebookBtn.setReadPermissions(Arrays.asList(EMAIL));
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(loginActivity, Arrays.asList("public_profile"));
                dialog.dismiss();
            }
        });

        LoginManager.getInstance().registerCallback(getCallbackManager(),
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        fetchFacebookUserName(new userNameCallback() {
                            public void onFullNameReceived(String fullName) {
                                callback.onSuccess(fullName);
                                Intent intent = new Intent(mContext, MainActivity.class);
                                mContext.startActivity(intent);
                                dialog.dismiss();
                            }
                        });
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
    }
    public CallbackManager getCallbackManager(){
        CallbackManager callbackManager = CallbackManager.Factory.create();
        return callbackManager;
    }
    public void showDangKy() {
        dialog_sigupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerDialog == null) {
                    registerDialog = new RegisterDialog((Context) mContext, callback);
                }
                registerDialog.showSignUpDialog();
            }
        });
    }
    public void showCheckEmailDialog() {
        dialog_forgotpasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forgotPasswordDialog == null) {
                    forgotPasswordDialog = new ForgotPasswordDialog((Context) mContext, callback);
                }
                forgotPasswordDialog.showCheckEmailDialog();
            }
        });
    }
    @SuppressLint("MissingInflatedId")
    public void showSignInDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialog_login = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(dialog_login);
        dialog = builder.create();
        dialog.show();
        loginButton = dialog_login.findViewById(R.id.loginButton);
        username = dialog_login.findViewById(R.id.username);
        password = dialog_login.findViewById(R.id.password);
        googleBtn = dialog_login.findViewById(R.id.google_button);
        facebookBtn = dialog_login.findViewById(R.id.facebook_button);
        dialog_forgotpasswordButton = dialog_login.findViewById(R.id.dialog_forgotpasswordButton);
        dialog_sigupButton = dialog_login.findViewById(R.id.dialog_sigupButton);
    }
}
