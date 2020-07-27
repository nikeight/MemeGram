package Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.instagramfinal.ChatActivity;
import com.example.instagramfinal.Post;
import com.example.instagramfinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.PostAdapter;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;

    private List<String> followingList;

    private ProgressBar progressBar;
    private ImageView chat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ProgressBar;

        progressBar= view.findViewById(R.id.progressBar);
        chat = view.findViewById(R.id.chat);

        // Starting ProgressBar.
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewPosts = view.findViewById(R.id.recyler_view_posts);

        // Setting Up recycler View. [For Posts]

        recyclerViewPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // So that we can see newest Post First.
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPosts.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        readPost();
        postAdapter = new PostAdapter(getContext(),postList);
        recyclerViewPosts.setAdapter(postAdapter);

        // Lists

        followingList = new ArrayList<>();

        CheckFollowingUsers();

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    private void CheckFollowingUsers() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance()
        .getCurrentUser().getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                // This to show the post of Current User to heir Home Fragments.

                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                readPost();
                // So that we can start seeing Posts of people we are following.

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPost() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);

                    for (String id: followingList){
                        if (post.getPublisher().equals(id)){
                            postList.add(post);

                        }
                    }
                }
                postAdapter.notifyDataSetChanged();

                // Ending Progressbar.
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
