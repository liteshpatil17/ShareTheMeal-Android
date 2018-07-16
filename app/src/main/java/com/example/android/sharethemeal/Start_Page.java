package com.example.android.sharethemeal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Start_Page extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start__page);

        Utility.prepareFonts(getApplicationContext());
        Utility.setStatusBar(getWindow(),getApplicationContext());
        Handler splash_handler=new Handler();
        splash_handler.postDelayed(SwitchActivity,1000);


    }

    private Runnable SwitchActivity=new Runnable() {
        @Override
        public void run() {
            if(Shared_Preferences_class.keep_logged_in_or_not(getApplicationContext())) {
                already_logged_in();
            }
            else
            {
                startActivity(new Intent(Start_Page.this,LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                finish();
            }

        }
    };

    public void already_logged_in()
    {
        firebaseAuth=FirebaseAuth.getInstance();
        Login_credentials login=Shared_Preferences_class.get_login_details(getApplicationContext());
        firebaseAuth.signInWithEmailAndPassword(login.email_id,login.password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(Start_Page.this,Main_start.class));
                    finish();

                }
                else
                {
                    try
                    {
                        throw task.getException();
                    }
                    catch(Exception e)
                    {
                        startActivity(new Intent(Start_Page.this,LoginActivity.class));
                        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                        finish();
                    }
                }
            }
        });
    }
}
