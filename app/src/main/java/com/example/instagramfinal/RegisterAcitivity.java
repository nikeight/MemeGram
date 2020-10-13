package com.example.instagramfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterAcitivity extends AppCompatActivity {

    //Initializing the layouts.
    private EditText username;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button registerNowButton;
    private TextView login_user;

    //Initializing the Firebase.
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acitivity);

        // Connecting the XML files.
        username = findViewById(R.id.username);
        name  = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        registerNowButton = findViewById(R.id.registerNowButton);
        login_user = findViewById(R.id.login_user);

        // Connecting Firebase
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();

        //When TextView is clicked.
        login_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterAcitivity.this,LoginActivity.class));
            }
        });

        //When registration Button is Clicked.
        registerNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textUsername = username.getText().toString();
                String textEmail = email.getText().toString();
                String textName = name.getText().toString();
                String textPassword = password.getText().toString();

                // To check if the fields are Empty or not.
                if (TextUtils.isEmpty(textUsername) || TextUtils.isEmpty(textEmail)
                        || TextUtils.isEmpty(textName) || TextUtils.isEmpty(textPassword)){
                    Toast.makeText(RegisterAcitivity.this, "Enter the Credentials Properly", Toast.LENGTH_SHORT).show();
                }
                else if(textPassword.length() < 6){
                    Toast.makeText(RegisterAcitivity.this, "Password is too short!", Toast.LENGTH_SHORT).show();
                }
                else{
                    registerUser(textUsername,textName,textEmail,textPassword);
                }
            }
        });
    }

    private void registerUser(final String username, final String name, final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                //Putting values to DB through HashMap.
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("name",name);
                userMap.put("email",email);
                userMap.put("username",username);
                userMap.put("id",mAuth.getCurrentUser().getUid());
                userMap.put("bio","");
                userMap.put("imageurl","default");

                // To add the data and to replace the fragment aat the same time.
                mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterAcitivity.this, "Update Your profile, dumbo" , Toast.LENGTH_SHORT).show();
                            Intent mainIntent = new Intent(RegisterAcitivity.this,StartMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterAcitivity.this, e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }
}


