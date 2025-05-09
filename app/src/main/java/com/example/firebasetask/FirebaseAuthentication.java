package com.example.firebasetask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/*
* Service class to perform Firebase Authentication
*/

public class FirebaseAuthentication {
    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthentication(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public FirebaseAuthentication() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Authenticate an existing user with email and password
     *
     * @param email User's email
     * @param password User's password
     * @param callback Callback to handle the result
     */
    public void authenticateUser(String email, String password, AuthCallback callback){
        // Validate inputs
        if (email == null || email.trim().isEmpty()) {
            callback.onFailure(new AuthError(AuthError.Type.EMPTY_EMAIL, "Email cannot be empty"));
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            callback.onFailure(new AuthError(AuthError.Type.EMPTY_PASSWORD, "Password cannot be empty"));
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            callback.onSuccess(user);
                        } else {
                            handleLoginError(task.getException(), callback);
                        }
                    }
                });
    }

    /**
     * Handles authentication errors using try-catch blocks
     *
     * @param exception The exception thrown during authentication
     * @param callback Callback to handle the result
     */
    private void handleLoginError(Exception exception, AuthCallback callback) {
        try {
            throw exception;
        } catch (FirebaseAuthInvalidCredentialsException e) {
            try {
                String errorCode = e.getErrorCode();
                if (errorCode != null && errorCode.equals("ERROR_INVALID_EMAIL")) {
                    callback.onFailure(new AuthError(AuthError.Type.INVALID_EMAIL,
                            "The email address is badly formatted."));
                } else {
                    callback.onFailure(new AuthError(AuthError.Type.INVALID_CREDENTIALS,
                            "Email or password is incorrect"));
                }
            } catch (Exception ex) {
                // If there's any issue accessing the error code, default to generic credentials error
                callback.onFailure(new AuthError(AuthError.Type.INVALID_CREDENTIALS,
                        "Email or password is incorrect"));
            }
        } catch (FirebaseAuthInvalidUserException e) {
            callback.onFailure(new AuthError(AuthError.Type.USER_NOT_FOUND,
                    "There is no user record corresponding to this identifier."));
        } catch (FirebaseNetworkException e) {
            callback.onFailure(new AuthError(AuthError.Type.NETWORK_ERROR,
                    "A network error has occurred."));
        } catch (FirebaseTooManyRequestsException e) {
            callback.onFailure(new AuthError(AuthError.Type.TOO_MANY_REQUESTS,
                    "We have blocked all requests from this device due to unusual activity."));
        } catch (Exception e) {
            callback.onFailure(new AuthError(AuthError.Type.UNKNOWN,
                    "Authentication failed: " + e.getMessage()));
        }
    }
}
