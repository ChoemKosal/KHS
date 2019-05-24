package com.example.khs.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private ImageView ImgUserPhoto;
    static  int PReqCode;
    private Uri peckedImgUrl;
    private EditText userEmail, userPassword, userPassword2, userName;
    private ProgressBar loadingProgress;
    private Button regBtn;
    private FirebaseAuth mAuth;
    private Intent Home;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //in value
        userEmail = findViewById(R.id.regMail);
        userPassword =findViewById(R.id.regPassword);
        userPassword2 = findViewById(R.id.regPassword2);
        userName = findViewById(R.id.regName);
        loadingProgress = findViewById(R.id.regProgressBar);
        regBtn = findViewById(R.id.regBtn);
        loadingProgress.setVisibility(View.INVISIBLE);
        Home = new Intent(this, com.example.khs.Activities.Home.class);
        mAuth = FirebaseAuth.getInstance();


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();
                final String name = userName.getText().toString();

                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || !password.equals(password2)){


                    //something goes wrong :all fields must be filled
                    //we need to display an error message
                    showMessage("Please Verify all fields");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);

                }
                else
                {
                    //everything is ok and all fields are fields now we cn start creating user account
                    //createUserAccount method will try to create the user if email is valid

                    CreateUserAccount(email,name,password);
                }

            }
        });



        ImgUserPhoto = findViewById(R.id.regUserPhoto);

        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 22 ){

                    checkAndRequestForPermission();

                }
                else
                {
                    openGallery();
                }
            }
        });
    }

    private void CreateUserAccount(String email, final String name, String password) {

        // this method crate user Account with specific email and password

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            //user Account created successfully
                            showMessage("Account created");
                            //after we create user account we need to update his profile and name
                            updateUserInfo(name , peckedImgUrl, mAuth.getCurrentUser());

                        }
                        else
                        {
                            // account creation failed
                            showMessage("Account creation failed" + task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }

                    }

                });



    }

    //update user photo and name
    private void updateUserInfo(final String name, Uri peckedImgUrl, final FirebaseUser currentUser) {

        // first we need to update user photo to firebase storage and get url
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_photos");
        final StorageReference imageFilePath = mStorage.child(peckedImgUrl.getLastPathSegment());
        imageFilePath.putFile(peckedImgUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //image uploaded successfully
                //now  we can get our image url

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //url contain user image url

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful())
                                        {
                                            //user info update successfully
                                            showMessage("Register Complete");
                                            updateUI();
                                        }
                                    }
                                });

                    }
                });



            }
        });


    }


    private void updateUI() {

        startActivity(Home);
        finish();

    }


    // simple method to show toast message
    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {
        //TODO open gallery intent and wait for user to piok an image

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("Image/jpg");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(galleryIntent,REQUEST_CODE);
    }


    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(RegisterActivity.this, "Please accept for required permission " , Toast.LENGTH_SHORT ).show();
            }
            else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this ,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
        else
            openGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&  requestCode == REQUEST_CODE && data != null ) {
            //the user has successfully picked as image
            // as send to save its reference  to a Url variable
            peckedImgUrl = data.getData();
            ImgUserPhoto.setImageURI(peckedImgUrl);
        }

    }
}
