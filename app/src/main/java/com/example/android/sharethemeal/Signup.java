package com.example.android.sharethemeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import static com.example.android.sharethemeal.Utility.AbhayaLibre;

public class Signup extends AppCompatActivity {
    EditText email,password,re_password;
    Button signup_button;

    TextView appName, title;

    private FirebaseAuth fire_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Utility.setStatusBar(getWindow(),getApplicationContext());
        email=(EditText)findViewById(R.id.email_signup);
        password=(EditText)findViewById(R.id.password_signup);
        signup_button=(Button)findViewById(R.id.signup_button);
        re_password=(EditText)findViewById(R.id.reenterpassword_signup);

        fire_auth=FirebaseAuth.getInstance();

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().toString().equals(re_password.getText().toString()))
                {
                    try_signing_up_user();
                }
                else
                {
                    Toast.makeText(Signup.this, "Both passwords should match", Toast.LENGTH_SHORT).show();
                }
            }
        });
        title=(TextView)findViewById(R.id.signup_title);
        title.setTypeface(AbhayaLibre);
        appName = (TextView)findViewById(R.id.signup_appName);
        appName.setTypeface(AbhayaLibre);
    }

    public void try_signing_up_user()
    {
        fire_auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(Signup.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    try{
                        throw task.getException();
                    }
                    catch(FirebaseNetworkException e)
                    {
                        Toast.makeText(Signup.this, "Network error.Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    catch(FirebaseAuthWeakPasswordException e)
                    {
                        Toast.makeText(Signup.this, "Password used is weak" , Toast.LENGTH_SHORT).show();
                    }
                    catch(FirebaseAuthUserCollisionException e)
                    {
                        Toast.makeText(Signup.this, "User already exists", Toast.LENGTH_SHORT).show();
                    }
                    catch(Exception e)
                    {
                        Log.e("other_exception","other_exception");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Signup.this,LoginActivity.class));
        finish();
        super.onBackPressed();
    }
}
