package com.example.mobileapp.ui.home;

public interface ConnectionCallback {

    public boolean status = false ;
    void onConnectionSuccess();

    void onConnectionFailure();

}
