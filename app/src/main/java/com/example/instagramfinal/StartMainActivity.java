package com.example.instagramfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Fragments.HomeFragment;
import Fragments.NotificationFragment;
import Fragments.ProfileFragment;
import Fragments.SearchFragment;

public class StartMainActivity extends AppCompatActivity {

    // Init.
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

     // FireBase
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Nav bar
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.homeBottom:
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.searchBottom:
                        selectorFragment = new SearchFragment();
                        break;

                    case R.id.addPostBottom:
                        selectorFragment = null;
                        startActivity(new Intent(StartMainActivity.this,PostActivity.class));
                        break;

                    case R.id.heartBottom:
                        selectorFragment = new NotificationFragment();
                        break;

                    case R.id.profileBottom:
                        selectorFragment = new ProfileFragment();
                        break;
                }

                    if (selectorFragment!= null){
                        //Replacing the fragments accordingly.
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                    }
                return true;
            }
        });

        Bundle intent = getIntent().getExtras();

        if(intent != null){

            String profileId= intent.getString("publisherId");
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId",profileId).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.profileBottom);

        }else{
            // As by default we want home fragment to show up.
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }
    }
}
