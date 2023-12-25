package com.example.javademo.authentication.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.javademo.R;
import com.example.javademo.authentication.callback.ILoginCallback;
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

public class LoginWithGoogle {
    private GoogleSignInClient gsc;
    private Context context;
    private ILoginCallback callback;
    private LoginActivity loginActivity;

    public LoginWithGoogle(Context context, ILoginCallback callback){
        this.context = context;
        this.callback = callback;
        this.loginActivity = (LoginActivity) context;
        initializeGoogleSignInClient();
    }

    private void initializeGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(context, gso);
    }

    public void loginWithGoogle() {
        final SignInButton googleBtn = loginActivity.getGoogleButton();
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = gsc.getSignInIntent();
                ((Activity) context).startActivityForResult(i, 1234);
            }
        });
    }

    public void googleSignOut() {
        gsc.signOut().addOnCompleteListener((Activity) context, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    public void handleGoogleSignInResult(Intent data){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
        if (acct != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    callback.onSuccess(acct.getDisplayName());
                                } else {
                                    callback.onFail(task.getException().getMessage());
                                }
                            }
                        });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
}
