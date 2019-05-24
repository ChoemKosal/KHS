package com.example.khs.Activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.khs.R;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class WelcomeActivity extends AppCompatActivity {

    private  final  static int REQUEST_CODE = 999;
    Button btn_userLoginPhone;
    private FirebaseAuth mAuth;
    private Intent Home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();
        Home = new Intent(this, com.example.khs.Activities.Home.class);
        btn_userLoginPhone = (Button)findViewById(R.id.btn_userLooIn);

        btn_userLoginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginPage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



    }

    private void startLoginPage() {
        Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); //use token when 'Enable Client Access Token Flow' is yes
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult( intent,REQUEST_CODE);
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

    private void updateUI() {
        startActivity(Home);
        finish();
    }


    public void onClickSupplierLogin(View view) {
        Intent register = new Intent(this, com.example.khs.Activities.LoginActivity.class);
        startActivity(register);
        finish();
    }
}
