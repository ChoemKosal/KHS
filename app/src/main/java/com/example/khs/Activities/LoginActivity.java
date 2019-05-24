package com.example.khs.Activities;


import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.khs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail,userPassword;
    private Button btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;

    private Intent Home;
    private Intent RegisterActivity;
    private ImageView logInPhoto;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.login_email);
        userPassword =findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.loginBtn);
        loginProgress = findViewById(R.id.login_progress);
        mAuth = FirebaseAuth.getInstance();
        Home = new Intent(this, com.example.khs.Activities.Home.class);
        RegisterActivity = new Intent(this, com.example.khs.Activities.RegisterActivity.class);
        logInPhoto = findViewById(R.id.login_photo);
        logInPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });


        loginProgress.setVisibility(View.INVISIBLE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View v) {

                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String mail = userEmail.getText().toString();
                final String password = userPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {
                    showMessage("Please Verify All Field");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }
                else
                {
                    signIn(mail,password);
                }
            }
        });
    }

    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();
                }
                else
                {
                    showMessage(task.getException().getMessage());
                    registerUI();
                }
            }
        });

    }


    private void registerUI() {

        startActivity(RegisterActivity);
        finish();
    }

    private void updateUI() {

        startActivity(Home);
        finish();

    }

    private void showMessage(String text) {

        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
        {
            //user is already connected so we need to redirect his home page
            updateUI();
        }

    }

    public void onClickRegister(View view) {

        Intent register = new Intent(this, com.example.khs.Activities.RegisterActivity.class);
        startActivity(register);
        finish();

    }
}
