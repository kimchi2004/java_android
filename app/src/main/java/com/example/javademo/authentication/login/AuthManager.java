package com.example.javademo.authentication.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.javademo.model.UserAccountModel;

public class AuthManager {
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_NAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static UserAccountModel userAccountModel;
    @SuppressLint("StaticFieldLeak")
    private static volatile AuthManager instance = null;

    public static AuthManager getInstance() {
        if (instance == null) {
            synchronized (AuthManager.class) {
                if (instance == null) {
                    instance = new AuthManager();
                }
            }
        }
        return instance;
    }


    public static UserAccountModel getUserAccountModel() {
        return userAccountModel;
    }

    public static void setUserAccountModel(UserAccountModel accountModel) {
        userAccountModel = accountModel;
    }

    public void saveLocal(Context mContext, UserAccountModel userAccountModel)
    {
        if(userAccountModel != null && userAccountModel.getUsername().length() >0) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,mContext.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NAME, userAccountModel.getUsername());
            editor.putString(KEY_PASSWORD, userAccountModel.getPassword());
            editor.putString(KEY_EMAIL, userAccountModel.getEmail());
            editor.apply();
            setUserAccountModel(userAccountModel);
        }
    }
    public UserAccountModel getLocalData(@NonNull Context mContext)
    {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        UserAccountModel userAccountModel = new UserAccountModel();
        userAccountModel.setUsername(sharedPreferences.getString(KEY_NAME, null));
        userAccountModel.setPassword(sharedPreferences.getString(KEY_PASSWORD, null));
        userAccountModel.setEmail(sharedPreferences.getString(KEY_EMAIL, null));
        setUserAccountModel(userAccountModel);
        return userAccountModel.getUsername() != null ? userAccountModel : null;
    }
}
