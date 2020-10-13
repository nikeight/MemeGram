package com.example.instagramfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    // Init
    private TextView post;
    private ImageView close;
    private ImageView image_added;
    SocialAutoCompleteTextView description;
    private ProgressBar progressBar;

    private Uri imageUri;
    private String imageUrl;

    private static final int GalleryCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        post= findViewById(R.id.post);
        close= findViewById(R.id.close);
        image_added= findViewById(R.id.image_added);
        description= findViewById(R.id.description);
        progressBar= findViewById(R.id.progressBar);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoStartMainActivity();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
        
       CropImage.activity().start(PostActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // To check if the result code is from Crop Activity Only.
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            image_added.setImageURI(imageUri);
        }
            else{
            Toast.makeText(this, "Try to upload Good Image", Toast.LENGTH_SHORT).show();
            gotoStartMainActivity();
        }
    }

    public void gotoStartMainActivity(){
        Intent mainIntent= new Intent(PostActivity.this,StartMainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void upload() {

        // Here was a ProgressDialogue Replace it with a ProgressBar.

        // Starting of Progressbar.

        progressBar.setVisibility(View.VISIBLE);
        if (imageUri != null){
            final StorageReference filePath= FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            StorageTask uploadTask= filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        progressBar.setVisibility(View.INVISIBLE);
                        return task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    //We will get the downloadURL from above return statements through task object.

                    Uri downloadUri = task.getResult();
                    imageUrl =downloadUri.toString(); // Storing the URL in a variable as a String.

                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
                    // We are generating a unique ID for every USer and Storing it to the db by using System Generated functions and databaseReference.
                    String postId = ref.push().getKey();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("postid",postId);
                    map.put("imageUrl",imageUrl);
                    map.put("description",description.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(postId).setValue(map);

                    // For HashTags.
                    DatabaseReference mHastTagRef= FirebaseDatabase.getInstance().getReference().child("HashTags");
                    List<String> hashTags = description.getHashtags();

                    // Why List? if there are more than one HashTags in a description.
                    if (!hashTags.isEmpty()){
                        for (String tag: hashTags){
                            map.clear(); // To clear the above data of the same Map. ( We can use another HahMap variable).

                            map.put("tag",tag.toLowerCase());
                            map.put("postid",postId);

                            mHastTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
                        }
                    }

                    // Ending of Progress Bar.

                    progressBar.setVisibility(View.INVISIBLE);
                    gotoStartMainActivity();
                    finish();

                    //So user don't get back to the post Activity.
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            Toast.makeText(this, "No Image was Selected. Try Again!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

}

