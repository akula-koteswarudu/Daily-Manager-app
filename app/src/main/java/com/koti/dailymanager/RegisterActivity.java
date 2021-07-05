package com.koti.dailymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;
    EditText registerUsername, registerPassword,registerMail,registerPhone,registerProfession;
    Button registerBtn;
    ProgressBar registerProgress;
    TextView loginHereBtn;
    DatabaseReference databaseReference;
    private static  int VERIFY_PHONE= 101;
    String userName;
    String email;
    String password;
    String phoneNumber;
    String profession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth= FirebaseAuth.getInstance();
        registerMail= findViewById(R.id.registerMail);
        registerPassword= findViewById(R.id.registerPassword);
        registerPhone= findViewById(R.id.registerPhoneNumber);
        registerProfession= findViewById(R.id.registerProfession);
        registerUsername= findViewById(R.id.registerUsername);
        registerBtn= findViewById(R.id.registerButton);
        registerProgress= findViewById(R.id.registerProgress);
        loginHereBtn= findViewById(R.id.loginHereBtn);
        databaseReference= FirebaseDatabase.getInstance().getReference("users");

        registerBtn.setOnClickListener(this);
        loginHereBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.loginHereBtn:
                Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.registerButton:
                registerUser();
                break;
        }
    }

    private void registerUser() {



        userName= registerUsername.getText().toString().trim();
        email = registerMail.getText().toString().trim();
        password= registerPassword.getText().toString().trim();
        phoneNumber= registerPhone.getText().toString().trim();
        profession = registerProfession.getText().toString().trim();

        if(userName.isEmpty()){
            Toast.makeText(this, "hihi", Toast.LENGTH_SHORT).show();
            registerUsername.setError("username is required");
            registerUsername.requestFocus();
            return;
        }

        if(email.isEmpty()){
            registerMail.setError("Email is required");
            registerMail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            registerMail.setError("Invalid email address");
            registerMail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            registerPassword.setError("password is required");
            registerPassword.requestFocus();
            return;
        }
        if(password.length()<8){
            registerPassword.setError("password should contain atleast 8 characters");
            registerPassword.requestFocus();
            return;
        }

        if(!phoneNumber.matches("^[0-9]+$")){
            registerPhone.setError("invalid phone Number");
            registerPhone.requestFocus();
            return;
        }

        if(phoneNumber.length()!=10){
            registerPhone.setError("phone Number should contain 10 numbers");
            registerPhone.requestFocus();
            return;
        }
        if(profession.isEmpty()){
            registerProfession.setError("profession is required");
            registerProfession.requestFocus();
            return;
        }

        Intent intent= new Intent(RegisterActivity.this,VerifyPhone.class);
        intent.putExtra("phone",phoneNumber);
        startActivityForResult(intent,VERIFY_PHONE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==VERIFY_PHONE && requestCode==RESULT_OK){
            register();
        }else{
            Toast.makeText(RegisterActivity.this,"registration failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void register() {
        registerProgress.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user= new User(userName,email,phoneNumber,profession);
                            databaseReference.child(auth.getCurrentUser().getUid()+"/credentials")
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"registered successfully, now login ",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(RegisterActivity.this,"registration failed",Toast.LENGTH_SHORT).show();
                                    }
                                    registerProgress.setVisibility(View.INVISIBLE);
                                }
                            });
                        }else{
                            registerProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this,"registered failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}