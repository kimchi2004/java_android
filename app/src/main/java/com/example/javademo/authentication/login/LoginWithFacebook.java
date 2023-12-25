package com.example.javademo.authentication.login;

import android.content.Context;
import android.os.Bundle;

import com.example.javademo.authentication.callback.ILoginCallback;
import com.example.javademo.authentication.callback.userNameCallback;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginWithFacebook {
    private Context context;
    private ILoginCallback callback;
    private LoginActivity loginActivity;
    private boolean isNameUpdated = false;

    public LoginWithFacebook(Context context, ILoginCallback callback){
        this.context = context;
        this.callback = callback;
        this.loginActivity = (LoginActivity) context;
    }

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
}
