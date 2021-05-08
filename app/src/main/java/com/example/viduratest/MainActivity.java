package com.example.viduratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.viduratest.Model.Register;
import com.example.viduratest.Utills.EncUtil;
import com.example.viduratest.Utills.Utill;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    Button submitBtn;
    EditText firstName, lastName, email, password, confirmPassword;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mContext = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24);
        submitBtn = findViewById(R.id.submitBtn);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(firstName.getText().toString())) {
                    firstName.setError(getString(R.string.first_name_required));
                    firstName.requestFocus();
                } else if (TextUtils.isEmpty(lastName.getText().toString())) {
                    lastName.setError(getString(R.string.first_name_required));
                    lastName.requestFocus();
                } else if (TextUtils.isEmpty(email.getText().toString())) {
                    email.setError(getString(R.string.first_name_required));
                    email.requestFocus();
                } else if (TextUtils.isEmpty(password.getText().toString())) {
                    password.setError(getString(R.string.first_name_required));
                    password.requestFocus();
                } else if (TextUtils.isEmpty(confirmPassword.getText().toString())) {
                    confirmPassword.setError(getString(R.string.first_name_required));
                    confirmPassword.requestFocus();
                } else {
                    if (email.getText().toString().matches(emailPattern)) {
                        if (password.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString())) {
                            try {
                               String encryptPass =  EncUtil.encrypt(password.getText().toString());

                                createNewUser(UUID.randomUUID().toString(), firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(),encryptPass, "", false);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Your password is not match", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        email.setError(getString(R.string.invalid_email));
                        email.requestFocus();
                    }
                }
            }
        });
    }

    // check user already logging with the facebook account
    public void createNewUser(String userId, String firstName, String lastName, String email, String password, String image, boolean isSocialMediaLogging) {
        if (Utill.isConnected(mContext)) {
            Register register = new Register(firstName, lastName, email, password, image, isSocialMediaLogging);

            mDatabase.child("users").child(userId).setValue(register)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Utill.sharedUserKey(mContext, userId);
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            intent.putExtra("key", userId);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_message), Toast.LENGTH_LONG).show();

        }
    }
}