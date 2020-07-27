package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramfinal.MainActivity;
import com.example.instagramfinal.R;
import com.example.instagramfinal.StartMainActivity;
import com.example.instagramfinal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.List;

import Fragments.ProfileFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUser;
    private boolean isFragment;

    public UserAdapter(Context mContext, List<User> mUser, boolean isFragment) {
        this.mContext = mContext;
        this.mUser = mUser;
        this.isFragment = isFragment;
    }

    private FirebaseUser firebaseUser;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user=mUser.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);

        holder.username.setText(user.getUsername());
        holder.name.setText(user.getName());

        // To load the image.( If there is no image a mipmap will be shown)
        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);

        //A method for Follow button.
        isFollowed(user.getId(),holder.btnFollow);

        //checking the user
        if (user.getId().equals(firebaseUser.getUid())){
            holder.btnFollow.setVisibility(View.GONE);
        }

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.btnFollow.getText().equals(("follow"))){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child((firebaseUser.getUid())).child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child((firebaseUser.getUid())).child("followers").child(firebaseUser.getUid()).setValue(true);

                    // If SomeOne follows you on Insta.

                    addNotification(user.getId());
                }else{

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child((firebaseUser.getUid())).child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child((firebaseUser.getUid())).child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        // To check if it is called bty fragment or Activity.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFragment){
                   mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                           .getString("profileId",user.getId());

                   //to replace the fragment.
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment()).commit();

                }else{
                    Intent intent = new Intent(mContext, StartMainActivity.class);
                    intent.putExtra("publisherId",user.getId());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    private void addNotification(String id) {

        HashMap<String,Object> map = new HashMap<>();

        map.put("userid",id);
        map.put("text", "Started Following you");
        map.put("postid","");
        map.put("isPost",false);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid())
                .push().setValue(map);
    }

    private void isFollowed(final String id, final Button btnFollow) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(id).exists())
                    btnFollow.setText("following");
                else
                    btnFollow.setText("follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // Initializing terms from user_item XML
        public CircleImageView imageProfile;
        public TextView username;
        public TextView name;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Linking XML file to java File.

            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.fullname);
            btnFollow = itemView.findViewById(R.id.btn_follow);
        }
    }
}
