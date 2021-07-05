package com.koti.dailymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN= 5000;

    Animation topAnim,bottomAnim;
    ImageView splashImage;
    TextView appName, slogan;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        splashImage= findViewById(R.id.splashImage);
        appName= findViewById(R.id.appName);
        slogan= findViewById(R.id.slogan);
        auth= FirebaseAuth.getInstance();

        splashImage.setAnimation(topAnim);
        appName.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(user!=null){
                            Intent intent= new Intent(MainActivity.this,DashboardActivity.class);
                            intent.putExtra("bool",false);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent= new Intent(MainActivity.this,LoginActivity.class);
                            intent.putExtra("bool",false);
                            startActivity(intent);
                            finish();
                        }
                    }
                },SPLASH_SCREEN);
            }
        };



    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null){
            auth.removeAuthStateListener(authStateListener);
        }
    }
}