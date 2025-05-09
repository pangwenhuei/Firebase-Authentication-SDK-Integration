package com.example.firebasetask;

import androidx.annotation.NonNull;

import java.util.Objects;

/*
 * Response error data by Firebase Authentication service
 */

public class AuthError {

    private final Type type;
    private final String message;

    public AuthError(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Enumeration of possible error types
     */
    public enum Type {
        INVALID_EMAIL,
        USER_NOT_FOUND,
        INVALID_CREDENTIALS,
        NETWORK_ERROR,
        TOO_MANY_REQUESTS,
        EMPTY_EMAIL,
        EMPTY_PASSWORD,
        UNKNOWN
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthError authError = (AuthError) o;
        return type == authError.type &&
                Objects.equals(message, authError.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, message);
    }

    @NonNull
    @Override
    public String toString() {
        return "AuthError{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}
