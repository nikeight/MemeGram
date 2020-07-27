package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramfinal.R;
import com.example.instagramfinal.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import Adapter.TagAdapter;
import Adapter.UserAdapter;

public class SearchFragment extends Fragment {

    // Search Fragment is not showing Users.

    // Recycler View for Users
    private RecyclerView recyclerView;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    // Recycler View for tags
    private RecyclerView recyclerViewTags;
    private List<String> mHashTags;
    private List<String> mHashTagsCount;
    private TagAdapter tagAdapter;


    private SocialAutoCompleteTextView search_bar;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Search bar of SocialTextView
        search_bar = view.findViewById(R.id.search_bar);

        // Initializing Recycler View and setting it up. [FOR USERS]
        recyclerView= view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Creating a ArrayList of users.
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(),mUsers,false);
        recyclerView.setAdapter(userAdapter);

        // Initializing Recycler View and setting it up. [FOR TAGS]
        recyclerViewTags= view.findViewById(R.id.recycler_tags);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));

        // Creating a ArrayLists for Tags.
        mHashTags = new ArrayList<>();
        mHashTagsCount = new ArrayList<>();
        tagAdapter = new TagAdapter(getContext(),mHashTags,mHashTagsCount);
        recyclerViewTags.setAdapter(tagAdapter);

        readUsers();

        readTags();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    private void readTags() {
        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mHashTags.clear();
                mHashTagsCount.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    //getChildren for iteration.
                   mHashTags.add(snapshot.getKey());
                   mHashTagsCount.add(dataSnapshot.getChildrenCount() + "");

                }

                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString())){
                    mUsers.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        //getChildren for iteration.
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUser(String s){

        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filter (String text) {
        List<String> mSearchTags = new ArrayList<>();
        List<String> mSearchTagsCount = new ArrayList<>();

        for (String s : mHashTags) {
            if (s.toLowerCase().contains(text.toLowerCase())){
                mSearchTags.add(s);
                mSearchTagsCount.add(mHashTagsCount.get(mHashTags.indexOf(s)));
            }
        }

        tagAdapter.filter(mSearchTags , mSearchTagsCount);
    }

}
