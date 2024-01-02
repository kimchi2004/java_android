package com.example.javademo.authentication.login;

import android.app.Activity;
import android.content.Context;

import com.example.javademo.MainActivity;
import com.example.javademo.authentication.callback.ILoginCallback;
import com.example.javademo.authentication.dialog.LoginDialog;
import com.example.javademo.model.UserAccountModel;

public class DangNhapDialog {
    private Context mContext;
    private MainActivity loginActivity;
    Activity mActivity;
    private static volatile DangNhapDialog instance = null;

    public static DangNhapDialog getInstance(){
        if(instance == null){
            synchronized (DangNhapDialog.class){
                if (instance == null){
                    instance = new DangNhapDialog();
                }
            }
        }
        return instance;
    }

    public void showsignIn(Activity mActivity, ILoginCallback iLoginCallback){
        UserAccountModel userAccountModel = AuthManager.getInstance().getLocalData((Context) mActivity);
        if (userAccountModel != null){
            iLoginCallback.onSuccess(userAccountModel.getUsername());
        }else {
            LoginDialog loginDialog = new LoginDialog((Context) mActivity, iLoginCallback);

            // open dialog
            loginDialog.showSignInDialog(mActivity);
            loginDialog.loginWithUsername();
            loginDialog.loginWithGoogle();
            loginDialog.loginWithFacebook();
            loginDialog.showDangKy();
            loginDialog.showCheckEmailDialog();
        }
    }
}
