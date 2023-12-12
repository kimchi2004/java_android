package com.example.javademo.authentication.login;

public interface ILoginCallback {
    void onSuccess(String username);
    void onFail(String message);
}
