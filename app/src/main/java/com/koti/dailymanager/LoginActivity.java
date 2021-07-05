package com.koti.dailymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView registerHereBtn ;
    EditText loginEmail,loginPassword;
    Button loginButton;
    ProgressBar loginProgress;
    FirebaseAuth auth;
    CallbackManager callbackManager;
    FirebaseAuth.AuthStateListener authStateListener;
    LoginButton facebookLoginBtn;
    DatabaseReference databaseReference;
    AccessTokenTracker accessTokenTracker;
    LoginManager loginManager;
    ImageButton googleBtn;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN =  4567;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googleBtn = findViewById(R.id.imageButton);
        createRequest();
        googleBtn.setOnClickListener(this);



        registerHereBtn= findViewById(R.id.registerHereBtn);
        loginEmail= findViewById(R.id.loginMail);
        loginPassword= findViewById(R.id.loginPassword);
        loginButton= findViewById(R.id.loginButton);
        loginProgress= findViewById(R.id.loginProgress);
        facebookLoginBtn= findViewById(R.id.facebookLoginBtn);
        facebookLoginBtn.setReadPermissions("email","public_profile");
        auth= FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        registerHereBtn.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        callbackManager= CallbackManager.Factory.create();
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        facebookLoginBtn.setOnClickListener(this);
        facebookLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("facebook","on success"+loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this,"cancelled",Toast.LENGTH_SHORT).show();
                loginProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(FacebookException error) {
                loginProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this,"some error occured"+error,Toast.LENGTH_SHORT).show();
            }
        });

        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= auth.getCurrentUser();
                if(user!=null){
                    UpdateUI(user);
                }
            }
        };

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken==null){
                    auth.signOut();
                }
            }
        };




    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("646509752625-alq5bat81q4pdl5111okl4glphuejnfl.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                Toast.makeText(this, "You Google account is connected to our application.", Toast.LENGTH_SHORT).show();
                //Intent i3 = new Intent( MainActivity.this,Sandy.class);
                // startActivity(i3);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            callbackManager.onActivityResult(requestCode,resultCode,data);
            facebookLoginBtn.setEnabled(false);
            facebookLoginBtn.setEnabled(false);
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user= auth.getCurrentUser();
                            UpdateUI(user);


                        } else {

                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }




    private void handleFacebookToken(AccessToken accessToken) {
        Log.d("facebook","handle token"+accessToken);
        AuthCredential credential= FacebookAuthProvider.getCredential(accessToken.getToken()) ;
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user= auth.getCurrentUser();
                    UpdateUI(user);
                }else{
                    Toast.makeText(LoginActivity.this,"login failed",Toast.LENGTH_SHORT).show();
                    UpdateUI(null);
                }
            }
        });


    }



    private void UpdateUI(FirebaseUser user) {
        if(user!=null){
            String username= user.getDisplayName();
            String email= user.getEmail();
            String phone= user.getPhoneNumber();
            String profession = "nothing";
            String uid= user.getUid();

            Intent intent= new Intent(LoginActivity.this,DashboardActivity.class);
            intent.putExtra("user object",new String[]{username,email,phone,profession,uid});
            intent.putExtra("bool",true);
            loginProgress.setVisibility(View.INVISIBLE);
            startActivity(intent);
            finish();



        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.registerHereBtn:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                break;
            case R.id.loginButton:
                userLogin();
                break;
            case R.id.facebookLoginBtn:
                loginProgress.setVisibility(View.VISIBLE);
                break;
            case R.id.imageButton:
                signIn();
                break;

        }
    }

    private void userLogin() {
        String email= loginEmail.getText().toString().trim();
        String password= loginPassword.getText().toString().trim();

        if(email.isEmpty()){
            loginEmail.setError("Email is required");
            loginEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("Invalid email address");
            loginEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            loginPassword.setError("password is required");
            loginPassword.requestFocus();
            return;
        }
        if(password.length()<8){
            loginPassword.setError("password should contain atleast 8 characters");
            loginPassword.requestFocus();
            return;
        }

        loginProgress.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,"login failed",Toast.LENGTH_SHORT).show();
                }
                loginProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
        facebookLoginBtn.setEnabled(true);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null){
            auth.removeAuthStateListener(authStateListener);
        }

    }
}