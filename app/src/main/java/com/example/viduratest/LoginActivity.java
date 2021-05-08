package com.example.viduratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.viduratest.Model.Register;
import com.example.viduratest.Utills.EncUtil;
import com.example.viduratest.Utills.Utill;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    RelativeLayout signBtn, registerBtn;
    EditText email, password;
    Context mContext;
    LoginButton fbLoginButton;
    CallbackManager callbackManager;
    String emailPattern;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        callbackManager = CallbackManager.Factory.create();
        signBtn = findViewById(R.id.signBtn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);
        fbLoginButton = findViewById(R.id.fb_login_button);
        emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if(Utill.isConnected(mContext)){
                    getUserProfile(AccessToken.getCurrentAccessToken());
                }else{
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet_connection_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancel() {

                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();

            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utill.isConnected(mContext)){
                    signIn();
                }else{
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet_connection_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserProfile(AccessToken currentAccessToken) {

            GraphRequest request = GraphRequest.newMeRequest(
                    currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                String first_name = object.getString("first_name");
                                String last_name = object.getString("last_name");
                                String id = object.getString("id");
                                String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                                if (!object.has("email")) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                                            .setTitle(getResources().getString(R.string.app_name));
                                    final FrameLayout frameView = new FrameLayout(mContext);
                                    builder.setView(frameView);

                                    final AlertDialog alertDialog = builder.create();
                                    LayoutInflater inflater = alertDialog.getLayoutInflater();
                                    View dialoglayout = inflater.inflate(R.layout.enter_email, frameView);
                                    alertDialog.show();

                                    EditText email = dialoglayout.findViewById(R.id.email);
                                    Button save = dialoglayout.findViewById(R.id.save);

                                    save.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (TextUtils.isEmpty(email.getText().toString())) {
                                                email.setError(getString(R.string.email_required));
                                                email.requestFocus();
                                            } else {
                                                if (email.getText().toString().matches(emailPattern)) {
                                                    checkIsAlreadyUserLogged(first_name, last_name, email.getText().toString(), "", image_url);
                                                }else{
                                                    email.setError(getString(R.string.invalid_email));
                                                    email.requestFocus();
                                                }
                                            }

                                        }
                                    });


                                } else {
                                    String email = object.getString("email");
                                    checkIsAlreadyUserLogged(first_name,last_name,email,"",image_url);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "first_name,last_name,email,id");
            request.setParameters(parameters);
            request.executeAsync();

    }

    //check user already logging with the facebook account
    public void checkIsAlreadyUserLogged(String first_name,String last_name,String email,String password,String image_url){
        Query query = mDatabase.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        String key = user.getKey();
                        Utill.sharedUserKey(mContext, key);
                        Intent intent = new Intent(mContext,ProfileActivity.class);
                        intent.putExtra("key",key);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    createNewUser(UUID.randomUUID().toString(), first_name, last_name, email, password, image_url, true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //sign in method
    public void signIn(){
        Query query = mDatabase.child("users").orderByChild("email").equalTo(email.getText().toString().trim());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        Register registeredUser = user.getValue(Register.class);
                        String key = user.getKey();
                        if(!registeredUser.isSocialMediaLogging()) {
                            try {
                                String encryptPass = EncUtil.encrypt(password.getText().toString());
                                if (registeredUser.password.equals(encryptPass)) {
                                    Utill.sharedUserKey(mContext, key);
                                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                    intent.putExtra("key", key);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Password is wrong", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }else{
                            Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // create new user and save data with database
    public void createNewUser(String userId, String firstName, String lastName, String email, String password, String image,boolean isSocialMediaLogging) {
        if (Utill.isConnected(mContext)) {
            Register register = new Register(firstName, lastName, email, password, image, isSocialMediaLogging);

            mDatabase.child("users").child(userId).setValue(register)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Utill.sharedUserKey(mContext, userId);
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra("key", userId);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();

                        }
                    });
        }else{
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet_connection_message), Toast.LENGTH_LONG).show();

        }
    }
}