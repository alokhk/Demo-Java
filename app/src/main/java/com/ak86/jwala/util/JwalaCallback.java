package com.ak86.jwala.util;

import android.content.Intent;

public interface JwalaCallback<T> {
    void onSuccess(T result);
    void onFailed(Integer errorCode);
}
