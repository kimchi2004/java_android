package com.example.javademo.authentication;

public interface ILoginCallback {
    void onSuccess(String username);
    void onFail(String message);
}
