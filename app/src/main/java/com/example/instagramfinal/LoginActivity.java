package com.example.instagramfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Initializing the Layouts.
    private EditText email;
    private EditText password;
    private TextView register_user;
    private Button loginNowButton;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Connecting the layouts with XML file.
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        register_user= findViewById(R.id.register_user);
        loginNowButton= findViewById(R.id.loginNowButton);

        mAuth= FirebaseAuth.getInstance();

        //When TextView Clicked.
        register_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterAcitivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(registerIntent);
            }
        });

        // When Login Button is pressed.
        loginNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = email.getText().toString();
                String textPassword = password.getText().toString();

                if (TextUtils.isEmpty(textEmail) || TextUtils.isEmpty(textPassword)){
                    Toast.makeText(LoginActivity.this, "Enter the Credentials Properly.", Toast.LENGTH_SHORT).show();
                }
                else{
                    loginUser(textEmail,textPassword);
                }
            }
        });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "Update Your profile, dumbo" , Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(LoginActivity.this,StartMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }
}
