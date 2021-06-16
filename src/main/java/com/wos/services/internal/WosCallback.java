package com.wos.services.internal;

interface WosCallback<T, K extends Exception> {

    public void onSuccess(T result);

    public void onFailure(K e);
}
