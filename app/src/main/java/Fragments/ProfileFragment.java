package Fragments;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagramfinal.EditProfileActivity;
import com.example.instagramfinal.FollowersActivity;
import com.example.instagramfinal.LoginActivity;
import com.example.instagramfinal.OptionsActivity;
import com.example.instagramfinal.Post;
import com.example.instagramfinal.R;
import com.example.instagramfinal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.interfaces.DSAKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapter.PhotoAdapter;
import Adapter.PostAdapter;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    // For My Saved Photos.
    private  RecyclerView recyclerViewSaves;
    private PhotoAdapter postAdapterSaves;
    private  List<Post> mySavedPosts;

    // For My Photos
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoList;

    private CircleImageView imageProfile;
    private ImageView options;
    private TextView followers;
    private TextView following;
    private TextView username;
    private TextView bio;
    private TextView fullname;
    private TextView posts;
    private Button editProfile;

    private ImageView myPictures;
    private ImageView savedPictures;

    private FirebaseUser fUser;
    String profileId;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_profile, container, false);

        // Firebase init.
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        profileId= fUser.getUid();


        // Shared Preference for onClick profile's
        String data= getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId","none");

        if (data.equals("none")){
            profileId= fUser.getUid();
        }else{
            profileId= data;
            // So that the original Profile get back to the profile Fragment.
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }

        // Layout Initialization.
        imageProfile= view.findViewById(R.id.image_profile);
        options= view.findViewById(R.id.options);
        followers= view.findViewById(R.id.followers);
        following= view.findViewById(R.id.following);
        username= view.findViewById(R.id.username);
        bio= view.findViewById(R.id.bio);
        fullname= view.findViewById(R.id.fullname);
        posts = view.findViewById(R.id.posts);
        myPictures= view.findViewById(R.id.my_pictures);
        savedPictures= view.findViewById(R.id.saved_pictures);
        editProfile =view.findViewById(R.id.edit_profile);

        // RecyclerView Init. for my Photos
        recyclerView= view.findViewById(R.id.recyler_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(),myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        // RecyclerView init. for my Saved Photos.
        recyclerViewSaves= view.findViewById(R.id.recyler_view_saved);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(),3));
        mySavedPosts = new ArrayList<>();
        postAdapterSaves = new PhotoAdapter(getContext(),mySavedPosts);
        recyclerViewSaves.setAdapter(postAdapterSaves);

        // Copy and paste leads to error ;)


        // Calling Various function according to the actions;
        userInfo();
        getFollowersAndFollowings();
        getPostCount();
        myPhotos();
        getSavedPhotos();

        if (profileId.equals(fUser.getUid())){
            editProfile.setText("Edit Profile");
        }else{
            CheckFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String btn_txt= editProfile.getText().toString();

                if (btn_txt.equals("Edit Profile")){

                    Intent EditIntent = new Intent(getContext(), EditProfileActivity.class);
                    startActivity(EditIntent);

                }else{
                    if (btn_txt.equals("Follow")){
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(fUser.getUid()).child("following").setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(profileId).child("followers").child(fUser.getUid()).setValue(true);
                    }
                    else{
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(fUser.getUid()).child("following").removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(profileId).child("followers").child(fUser.getUid()).removeValue();
                    }
                }

            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);

        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
            }
        });

        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });


        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","followers");
                startActivity(intent);
            }
        });


        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","following");
                startActivity(intent);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });

        return view;
    }

    private void getSavedPhotos() {

        final  List<String> savedIds= new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Saves").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    savedIds.add(snapshot.getKey());
                }

                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        mySavedPosts.clear();

                        for (DataSnapshot snapshot1: dataSnapshot1.getChildren()){
                            Post post = snapshot1.getValue(Post.class);

                            for (String id: savedIds){
                                if (post.getPostid().equals(id)){
                                    mySavedPosts.add(post);
                                }
                            }
                        }

                        postAdapterSaves.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myPhotos() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPhotoList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);

                    if (post.getPublisher().equals(profileId)){
                        // To check if the Current USer has made the post and add it to a list.
                        myPhotoList.add(post);

                    }
                }
                // It is to reverse the list as we want the newest Post First.
                Collections.reverse(myPhotoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CheckFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileId).exists()){
                    editProfile.setText("Following");
                }
                else{
                    editProfile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getPostCount() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter=0;
                for (DataSnapshot snapshots: dataSnapshot.getChildren()){
                    Post post = snapshots.getValue(Post.class);

                    // error fixed of posts and following.
                    if (post.getPublisher().equals(profileId)){
                        counter++;
                    }

                }
                posts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowersAndFollowings() {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);

        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void userInfo() {


        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);

                Picasso.get().load(user.getImageurl()).into(imageProfile);
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                fullname.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
