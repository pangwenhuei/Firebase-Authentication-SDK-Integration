package com.example.firebasetask;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FirebaseAuthenticationTest {
    @Mock
    private FirebaseAuth mockFirebaseAuth;
    @Mock
    private Task<AuthResult> mockTask;

    @Mock
    private FirebaseUser mockUser;

    private FirebaseAuthentication authService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        authService = new FirebaseAuthentication(mockFirebaseAuth);
    }

    @Test
    public void testAuthenticateUser_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "Password123";
        AuthCallback mockCallback = mock(AuthCallback.class);

        when(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockUser);

        // Capture the OnCompleteListener to trigger it manually
        ArgumentCaptor<OnCompleteListener> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        doAnswer(invocation -> {
            listenerCaptor.getValue().onComplete(mockTask);
            return mockTask;
        }).when(mockTask).addOnCompleteListener(listenerCaptor.capture());

        // Act
        authService.authenticateUser(email, password, mockCallback);

        // Assert
        verify(mockCallback).onSuccess(mockUser);
        verify(mockCallback, never()).onFailure(any(AuthError.class));
    }

    @Test
    public void testAuthenticateUser_EmptyEmail() {
        // Arrange
        String email = "";
        String password = "Password123";
        AuthCallback mockCallback = mock(AuthCallback.class);

        // Act
        authService.authenticateUser(email, password, mockCallback);

        // Assert
        verify(mockCallback).onFailure(argThat(error ->
                error.getType() == AuthError.Type.EMPTY_EMAIL &&
                        error.getMessage().equals("Email cannot be empty")));
        verify(mockFirebaseAuth, never()).signInWithEmailAndPassword(anyString(), anyString());
    }

    @Test
    public void testAuthenticateUser_NullEmail() {
        // Arrange
        String email = null;
        String password = "Password123";
        AuthCallback mockCallback = mock(AuthCallback.class);

        // Act
        authService.authenticateUser(email, password, mockCallback);

        // Assert
        verify(mockCallback).onFailure(argThat(error ->
                error.getType() == AuthError.Type.EMPTY_EMAIL &&
                        error.getMessage().equals("Email cannot be empty")));
        verify(mockFirebaseAuth, never()).signInWithEmailAndPassword(anyString(), anyString());
    }

    @Test
    public void testAuthenticateUser_EmptyPassword() {
        // Arrange
        String email = "test@example.com";
        String password = "";
        AuthCallback mockCallback = mock(AuthCallback.class);

        // Act
        authService.authenticateUser(email, password, mockCallback);

        // Assert
        verify(mockCallback).onFailure(argThat(error ->
                error.getType() == AuthError.Type.EMPTY_PASSWORD &&
                        error.getMessage().equals("Password cannot be empty")));
        verify(mockFirebaseAuth, never()).signInWithEmailAndPassword(anyString(), anyString());
    }

    @Test
    public void testAuthenticateUser_NullPassword() {
        // Arrange
        String email = "test@example.com";
        String password = null;
        AuthCallback mockCallback = mock(AuthCallback.class);

        // Act
        authService.authenticateUser(email, password, mockCallback);

        // Assert
        verify(mockCallback).onFailure(argThat(error ->
                error.getType() == AuthError.Type.EMPTY_PASSWORD &&
                        error.getMessage().equals("Password cannot be empty")));
        verify(mockFirebaseAuth, never()).signInWithEmailAndPassword(anyString(), anyString());
    }

    @Test
    public void testAuthenticateUser_InvalidEmail() {
        // Arrange
        String email = "test@example.com";
        String password = "Password123";
        AuthCallback mockCallback = mock(AuthCallback.class);
        Exception mockException = new FirebaseAuthInvalidCredentialsException("INVALID_EMAIL", "The email address is badly formatted.");

        when(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);
        when(mockTask.getException()).thenReturn(mockException);

        // Capture the OnCompleteListener to trigger it manually
        ArgumentCaptor<OnCompleteListener> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        doAnswer(invocation -> {
            listenerCaptor.getValue().onComplete(mockTask);
            return mockTask;
        }).when(mockTask).addOnCompleteListener(listenerCaptor.capture());

        // Act
        authService.authenticateUser(email, password, mockCallback);

        // Assert
        verify(mockCallback).onFailure(argThat(error ->
                error.getType() == AuthError.Type.INVALID_CREDENTIALS &&
                        error.getMessage().equals("Email or password is incorrect")));
    }

    @Test
    public void testAuthenticateUser_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "Password123";
        AuthCallback mockCallback = mock(AuthCallback.class);
        Exception mockException = new FirebaseAuthInvalidUserException("USER_NOT_FOUND", "There is no user record corresponding to this identifier.");

        when(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);
        when(mockTask.getException()).thenReturn(mockException);

        // Capture the OnCompleteListener to trigger it manually
        ArgumentCaptor<OnCompleteListener> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        doAnswer(invocation -> {
            listenerCaptor.getValue().onComplete(mockTask);
            return mockTask;
        }).when(mockTask).addOnCompleteListener(listenerCaptor.capture());

        // Act
        authService.authenticateUser(email, password, mockCallback);

        // Assert
        verify(mockCallback).onFailure(argThat(error ->
                error.getType() == AuthError.Type.USER_NOT_FOUND &&
                        error.getMessage().equals("There is no user record corresponding to this identifier.")));
    }

    @Test
    public void testAuthenticateUser_InvalidCredentials() {
        // Arrange
        String email = "test@example.com";
        String password = "WrongPassword";
        AuthCallback mockCallback = mock(AuthCallback.class);
        Exception mockException = new FirebaseAuthInvalidCredentialsException("INVALID_PASSWORD", "The password is invalid.");

        when(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);
        when(mockTask.getException()).thenReturn(mockException);

        // Capture the OnCompleteListener to trigger it manually
        ArgumentCaptor<OnCompleteListener> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        doAnswer(invocation -> {
            listenerCaptor.getValue().onComplete(mockTask);
            return mockTask;
        }).when(mockTask).addOnCompleteListener(listenerCaptor.capture());

        // Act
        authService.authenticateUser(email, password, mockCallback);

        // Assert
        verify(mockCallback).onFailure(argThat(error ->
                error.getType() == AuthError.Type.INVALID_CREDENTIALS &&
                        error.getMessage().equals("Email or password is incorrect")));
    }

    @Test
    public void testAuthenticateUser_NetworkError() {
        // Arrange
        String email = "test@example.com";
        String password = "Password123";
        AuthCallback mockCallback = mock(AuthCallback.class);
        Exception mockException = new FirebaseNetworkException("NETWORK_ERROR");

        when(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);
        when(mockTask.getException()).thenReturn(mockException);

        // Capture the OnCompleteListener to trigger it manually
        ArgumentCaptor<OnCompleteListener> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        doAnswer(invocation -> {
            listenerCaptor.getValue().onComplete(mockTask);
            return mockTask;
        }).when(mockTask).addOnCompleteListener(listenerCaptor.capture());

        // Act
        authService.authenticateUser(email, password, mockCallback);

        // Assert
        verify(mockCallback).onFailure(argThat(error ->
                error.getType() == AuthError.Type.NETWORK_ERROR &&
                        error.getMessage().equals("A network error has occurred.")));
    }

    @Test
    public void testAuthenticateUser_TooManyRequests() {
        // Arrange
        String email = "test@example.com";
        String password = "Password123";
        AuthCallback mockCallback = mock(AuthCallback.class);
        Exception mockException = new FirebaseTooManyRequestsException("TOO_MANY_REQUESTS");

        when(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);
        when(mockTask.getException()).thenReturn(mockException);

        // Capture the OnCompleteListener to trigger it manually
        ArgumentCaptor<OnCompleteListener> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        doAnswer(invocation -> {
            listenerCaptor.getValue().onComplete(mockTask);
            return mockTask;
        }).when(mockTask).addOnCompleteListener(listenerCaptor.capture());

        // Act
        authService.authenticateUser(email, password, mockCallback);

        // Assert
        verify(mockCallback).onFailure(argThat(error ->
                error.getType() == AuthError.Type.TOO_MANY_REQUESTS &&
                        error.getMessage().equals("We have blocked all requests from this device due to unusual activity.")));
    }

    @Test
    public void testAuthenticateUser_UnknownError() {
        // Arrange
        String email = "test@example.com";
        String password = "Password123";
        AuthCallback mockCallback = mock(AuthCallback.class);
        Exception mockException = new RuntimeException("Something unexpected happened");

        when(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);
        when(mockTask.getException()).thenReturn(mockException);

        // Capture the OnCompleteListener to trigger it manually
        ArgumentCaptor<OnCompleteListener> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        doAnswer(invocation -> {
            listenerCaptor.getValue().onComplete(mockTask);
            return mockTask;
        }).when(mockTask).addOnCompleteListener(listenerCaptor.capture());

        // Act
        authService.authenticateUser(email, password, mockCallback);

        // Assert
        verify(mockCallback).onFailure(argThat(error ->
                error.getType() == AuthError.Type.UNKNOWN &&
                        error.getMessage().equals("Authentication failed: Something unexpected happened")));
    }
}
