package com.example.javademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javademo.authentication.callback.ILoginCallback;
import com.example.javademo.authentication.callback.userNameCallback;
import com.example.javademo.authentication.dialog.LoginDialog;
import com.example.javademo.authentication.login.AuthManager;
import com.example.javademo.authentication.login.DangNhapDialog;
import com.example.javademo.model.UserAccountModel;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView login_name;
    Button loginButton;
    FirebaseAuth mAuth;
    private LoginDialog loginDialog;
    private ILoginCallback iLoginCallback;

    //onCreate
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        login_name = findViewById(R.id.login_name);
        loginButton = findViewById(R.id.showDialogLoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.logoutUsername();
                mAuth.signOut();
                loginDialog.googleSignOut();
                LoginManager.getInstance().logOut();
                DangNhapDialog.getInstance().showsignIn(MainActivity.this, iLoginCallback);
            }
        });
        Activity mActivity = this;
        iLoginCallback = new ILoginCallback() {
            @Override
            public void onSuccess(String username) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "Login Successfull! " + username, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFail(String message) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        loginDialog = new LoginDialog(this, iLoginCallback);

        //username
        mAuth = FirebaseAuth.getInstance();
        AuthManager.getInstance().getLocalData(this);
        UserAccountModel userAccountModel = AuthManager.getUserAccountModel();
        if(userAccountModel != null){
            updateLoginName(userAccountModel.getUsername());
        }

        //google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            updateLoginName(personName);
        }

        //facebook
        loginDialog.fetchFacebookUserName(new userNameCallback() {
            public void onFullNameReceived(String fullName) {
                updateLoginName(fullName);
            }
        });
    }
    //onActivityResult------------------
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (loginDialog.getCallbackManager() != null) {
            loginDialog.getCallbackManager().onActivityResult(requestCode, resultCode, data);
            loginDialog.fetchFacebookUserName(new userNameCallback() {
                public void onFullNameReceived(String fullName) {
                    updateLoginName(fullName);
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            loginDialog.handleGoogleSignInResult(data);
        }
    }
    public void updateLoginName(String name) {
        login_name.setText(name);
    }
}
