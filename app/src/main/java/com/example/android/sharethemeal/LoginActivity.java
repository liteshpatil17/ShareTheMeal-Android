package com.example.android.sharethemeal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.example.android.sharethemeal.Utility.AbhayaLibre;
import static com.example.android.sharethemeal.Utility.nunito_bold;
import static com.example.android.sharethemeal.Utility.nunito_reg;


public class LoginActivity extends AppCompatActivity {
    Button login;
    EditText emailid_login, password_login;
    TextView forgot_password, sign_up, title, appName;
    private FirebaseAuth fire_auth;
    ImageButton toggleButton;
    boolean isPasswordShown = false;
    private SignInButton mGoogleSignIn;
    ProgressDialog progress;
    private static int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);

        fire_auth = FirebaseAuth.getInstance();
        Utility.setStatusBar(getWindow(), getApplicationContext());

        login = (Button) findViewById(R.id.login);
        emailid_login = (EditText) findViewById(R.id.email_login);
        password_login = (EditText) findViewById(R.id.password_login);
        forgot_password = (TextView) findViewById(R.id.forgot_password);
        sign_up = (TextView) findViewById(R.id.sign_up);
        sign_up.setTypeface(nunito_reg);
        forgot_password.setTypeface(nunito_reg);
        emailid_login.setTypeface(nunito_reg);
        password_login.setTypeface(nunito_reg);
        progress = new ProgressDialog(this);
        mGoogleSignIn = (SignInButton) findViewById(R.id.googleSignIn);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, Signup.class));
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailid_login.getText().toString().length() > 0 && password_login.getText().toString().length() > 0) {
                    progress.setMessage("Signing you in..");
                    progress.setIndeterminate(true);
                    progress.show();
                    sign_user_in();
                } else
                    Toast.makeText(LoginActivity.this, "Email-ID and password must not be empty", Toast.LENGTH_SHORT).show();
            }
        });
        login.setTypeface(nunito_bold);

        toggleButton = (ImageButton) findViewById(R.id.toggle_password);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toogleShowPassword();
            }
        });

        title = (TextView) findViewById(R.id.login_title);
        title.setTypeface(AbhayaLibre);
        appName = (TextView) findViewById(R.id.login_appName);
        appName.setTypeface(AbhayaLibre);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "You Got an Error", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginActivity.this, Main_start.class));
                }
            }
        };




        //forgot pssword
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, reset_password.class));
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        fire_auth.addAuthStateListener(mAuthStateListener);
    }

    void toogleShowPassword() {
        if (isPasswordShown) {
            //DONT SHOW PASSWORD
            password_login.setTransformationMethod(new PasswordTransformationMethod());
            Glide.with(getApplicationContext()).load(R.drawable.ic_show_password).into(toggleButton);
            isPasswordShown = false;
            password_login.setSelection(password_login.getText().length());
        } else {
            //SHOW PASSWORD
            password_login.setTransformationMethod(null);
            Glide.with(getApplicationContext()).load(R.drawable.ic_unshow_password).into(toggleButton);
            isPasswordShown = true;
            password_login.setSelection(password_login.getText().length());
        }
    } //SHOW AND INSHOW PASSWORD CONTROLLER

    public void sign_user_in() {
        fire_auth.signInWithEmailAndPassword(emailid_login.getText().toString(), password_login.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Login_credentials login = new Login_credentials();
                    login.email_id = emailid_login.getText().toString();
                    login.password = password_login.getText().toString();
                    Shared_Preferences_class.put_login_details(getApplicationContext(), login);
                    Shared_Preferences_class.set_keep_logged_in(true, getApplicationContext());
                    startActivity(new Intent(LoginActivity.this, Main_start.class));
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseNetworkException e) {
                        Toast.makeText(LoginActivity.this, "Unable to connect.Please check your Internet connection", Toast.LENGTH_LONG).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        password_login.setText("");
                    } catch (Exception e) {
                        Log.e("tag", e.getMessage());
                    }
                }
                progress.cancel();
            }
        });
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(" ", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fire_auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(null, "signInWithCredential:success");
                        if (!task.isSuccessful()) {
                            Log.w(" ", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}



