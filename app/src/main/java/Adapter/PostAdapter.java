package Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramfinal.CommentActivity;
import com.example.instagramfinal.FollowersActivity;
import com.example.instagramfinal.Post;
import com.example.instagramfinal.R;
import com.example.instagramfinal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import Fragments.PhotoDetailFragment;
import Fragments.ProfileFragment;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        // Creating a Post object and Storing position in it. To show up at right position.

        final Post post= mPosts.get(position);

        // To load the Image. Picasso lib.

        Picasso.get().load(post.getImageUrl()).into(holder.postImage);
        holder.description.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // As we want and to show up the values of Users.
                User user =dataSnapshot.getValue(User.class);

                //As if there is no ImageURl app get Crashed.

                if (user.getImageurl().equals("default")){
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);

                }else{
                    Picasso.get().load(user.getImageurl()).into(holder.imageProfile);
                }

                holder.username.setText(user.getUsername());
                holder.author.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Calling Various Methods.
        isLiked(post.getPostid(),holder.like);
        noOfLikes(post.getPostid(),holder.noOfLikes);
        noOfComments(post.getPostid(),holder.noOfComments);
        isSaved(post.getPostid(),holder.save);

        // this setOnClick for Likes
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.like.getTag().equals("like")){
                        FirebaseDatabase.getInstance().getReference().child("Likes")
                                .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                        // Calling for Notification Function
                        addNotification(post.getPostid(),post.getPublisher());
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        // this setOnClick for Comments
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId",post.getPostid());
                intent.putExtra("authorId",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId",post.getPostid());
                intent.putExtra("authorId",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // See copy paste leads to error.
                // Saves -> User -> postId -> save the data.
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                }
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If it doesn't work change profileid to profileId.

                // Back button causing the app to get close not directing to the Home page of the App.

                mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new ProfileFragment()).commit();
                // putting Commit is necessary.
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new ProfileFragment()).commit();
                // putting Commit is necessary.
            }
        });

        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If it doesn't work change profileid to profileId.

                mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new ProfileFragment()).commit();
                // putting Commit is necessary.
            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postid",post.getPostid()).apply();
                // Edit to add the data, apply to run the statement, putString to put the data.

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new PhotoDetailFragment()).commit();
            }
        });

        holder.noOfLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",post.getPublisher());
                intent.putExtra("title","likes");
                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {

        return  mPosts.size();
        // If everything setUp make it to normal again.
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageProfile;
        public ImageView postImage;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;


        public TextView noOfLikes;
        public TextView author;
        public TextView description;
        public TextView noOfComments;
        public TextView username;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            more = itemView.findViewById(R.id.more);

            noOfLikes = itemView.findViewById(R.id.no_of_likes);
            noOfComments = itemView.findViewById(R.id.no_of_comments);
            username = itemView.findViewById(R.id.username);
            description = itemView.findViewById(R.id.description);
            author = itemView.findViewById(R.id.author);
        }
    }

    private  void isLiked(String postId, final ImageView imageView){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                         imageView.setImageResource(R.drawable.ic_liked);
                         imageView.setTag("liked");
                     }
                     else{
                         imageView.setImageResource(R.drawable.ic_like);
                         imageView.setTag("like");
                     }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private  void noOfLikes (String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText(dataSnapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void noOfComments(String postId, final TextView text){
        FirebaseDatabase.getInstance().getReference().child("Comment").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                text.setText(dataSnapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotification(final String postId,final String publisherId) {

        HashMap<String,Object> map = new HashMap<>();

        map.put("userid",publisherId);
        map.put("text", "liked your Post.");
        map.put("postid",postId);
        map.put("isPost",true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid())
                .push().setValue(map);

    }

    private void isSaved(final String postid, final ImageView image) {

        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(postid).exists()){
                            image.setImageResource(R.drawable.ic_save_black);
                            image.setTag("saved");
                        }
                        else{
                            image.setImageResource(R.drawable.ic_save);
                            image.setTag("save");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
