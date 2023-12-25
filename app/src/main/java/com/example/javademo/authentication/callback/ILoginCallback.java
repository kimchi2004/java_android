package com.example.javademo.authentication.callback;

public interface ILoginCallback {
    void onSuccess(String username);
    void onFail(String message);
}
