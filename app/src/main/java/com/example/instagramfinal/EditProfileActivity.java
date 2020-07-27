package com.example.instagramfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.net.Uri;
import android.opengl.ETC1;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private TextView changePhoto;
    private CircleImageView image_profile;
    private ImageView close;
    private TextView save;
    private MaterialEditText fullname;
    private MaterialEditText username;
    private MaterialEditText bio;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseUser fUser;

    // For New Cropped Image
    private Uri mImageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // FireBase
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        // Layout init.
        changePhoto = findViewById(R.id.changePhoto);
        image_profile = findViewById(R.id.image_profile);
        close = findViewById(R.id.close);
        save = findViewById(R.id.save);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        progressBar= findViewById(R.id.progressBar);

        // Init Other variables;
        storageRef= FirebaseStorage.getInstance().getReference().child("Uploads");

        // you can jump to the file while clicking the object while holding ctrl.

        // To get the previous data.
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);

                fullname.setText(user.getName());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                Picasso.get().load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {

        HashMap<String,Object> map = new HashMap<>();
        map.put("name",fullname.getText().toString());
        map.put("username",username.getText().toString());
        map.put("bio",bio.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).updateChildren(map);
        //progressbar to Stop here.
    }

    public void uploadImage(){
        // Here, to put ProgressBar;
        progressBar.setVisibility(View.VISIBLE);

        if (mImageUri != null){
            final StorageReference filePath = storageRef.child(System.currentTimeMillis()+ ".jpeg");
            uploadTask = filePath.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        progressBar.setVisibility(View.INVISIBLE);
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();

                        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).child("imageurl").setValue(url);
                        progressBar.setVisibility(View.INVISIBLE);

                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(EditProfileActivity.this, "Failed to upload the Image, Try again and again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // to check if the request is from this activity only and to verify the Image.
        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            // passing the data we received from the intent at the super.
            mImageUri = result.getUri();

            uploadImage();
        }else {
            Toast.makeText(this, "You haven't selected a image", Toast.LENGTH_SHORT).show();
        }
    }
}
