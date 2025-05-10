# Firebase-Authentication-SDK-Integration
 Integrate third-party SDKs using Maven(Gradle) and implement a basic user authentication flow using the Firebase Authentication SDK in a Java(Android Studio Meerkat) application

# Notes:
- Main authentication logic is written in FirebaseAuthentication.java class
- authenticateUser method has additional callback parameter so the caller can receive auth response and handle in context.
- 1 additional class called AuthCallback.java is used to store the response error data by Firebase Authentication service
- 1 additional interface AuthError.java for fascilitating error handling
- Unit tests are written using Mockito testing framework
- Testings written at FirebaseAuthenticationTest.java references my personal firebase account, with following generated user details:
- Username : test@example.com
- Password : Password123
<img width="929" alt="image" src="https://github.com/user-attachments/assets/77fdce8b-21db-45b9-a54b-dcb156c4d3c1" />
