package com.example.myfirebaseblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "mytag";

    private Button mButtonRegister;
    private EditText mFieldEmail;
    private EditText mFieldUserName;
    private EditText mFieldPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView mTextLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mTextLogin = findViewById(R.id.tv_register_login);
        mButtonRegister = findViewById(R.id.btn_register_register);
        mFieldEmail = findViewById(R.id.et_register_email);
        mFieldUserName = findViewById(R.id.et_register_username);
        mFieldPassword = findViewById(R.id.et_register_password);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mTextLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "Loading...", Toast.LENGTH_LONG).show();
                final String username = mFieldUserName.getText().toString().trim();
                final String password = mFieldPassword.getText().toString().trim();
                final String email = mFieldEmail.getText().toString().trim();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = mDatabase.child(userId);
                            currentUserDb.child("username").setValue(username);
                            currentUserDb.child("image").setValue("default");

                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            Intent regIntent = new Intent(RegisterActivity.this, ProfileActivity.class);
                            regIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(regIntent);
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Complete all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
