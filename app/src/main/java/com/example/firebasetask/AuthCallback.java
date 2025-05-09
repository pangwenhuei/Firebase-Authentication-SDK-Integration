package com.example.firebasetask;

import com.google.firebase.auth.FirebaseUser;

/*
* Response data by Firebase Authentication service
*/

public interface AuthCallback {
    void onSuccess(FirebaseUser user);
    void onFailure(AuthError error);
}
