package com.example.instagramfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.CommentAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private EditText add_a_comment;
    private TextView post;
    private CircleImageView image_profile;

    private String postId;
    private String authorId;

    private FirebaseUser fUser;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                // Direct to home page;
            }
        });

        // Intents
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");


        // Setting up the recyclerView.
        recyclerView= findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList= new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentList,postId);

        recyclerView.setAdapter(commentAdapter);
        

        add_a_comment = findViewById(R.id.add_a_comment);
        image_profile= findViewById(R.id.image_profile);
        post= findViewById(R.id.post);

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        // To get the profile Image of the USer.
        getUserImage();

        // To get the list of Comments.
        getComment();

        //When Post TV is hit.
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(add_a_comment.getText().toString())){
                    Toast.makeText(CommentActivity.this, "Add a comment first.", Toast.LENGTH_SHORT).show();
                }else{
                    putComment();

                }
            }
        });
        

    }

    private void getComment() {

        FirebaseDatabase.getInstance().getReference().child("Comment").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for ( DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void putComment() {

        HashMap<String,Object> map = new HashMap<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comment").child(postId);

        String id= ref.push().getKey();

        map.put("id",id);
        map.put("comment",add_a_comment.getText().toString());
        map.put("publisher",fUser.getUid());

        add_a_comment.setText("");

        // To get a random Id we use push();
        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(CommentActivity.this, "Comment added!!!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CommentActivity.this, "Not able to add a comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserImage() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                if (user.getImageurl().equals("")){ // If it is empty
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Picasso.get().load(user.getImageurl()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
