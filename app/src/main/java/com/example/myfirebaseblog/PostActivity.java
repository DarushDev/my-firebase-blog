package com.example.myfirebaseblog;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GALLERY = 2;
    private static final String TAG = "myTag";
    private Uri mUri = null;
    private ImageButton mImageBtnAddImage;
    private EditText mEtTitle;
    private EditText mEtDescription;
    private Button mBtnPost;
    private StorageReference mFirebaseStorage;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // initialize objects
        mBtnPost = findViewById(R.id.button_post_post);
        mEtDescription = findViewById(R.id.edittext_post_description);
        mEtTitle = findViewById(R.id.edittext_post_title);
        mImageBtnAddImage = findViewById(R.id.imagebutton_post_addImage);

        mFirebaseStorage = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("blog");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());

        // pick an image from gallery
        mImageBtnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
            }
        });

        // post to Firebase
        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PostActivity.this, "Posting...", Toast.LENGTH_LONG).show();

                final String postTitle = mEtTitle.getText().toString().trim();
                final String postDesc = mEtDescription.getText().toString().trim();

                // check for empty fields
                if(!TextUtils.isEmpty(postDesc) && !TextUtils.isEmpty(postTitle)){

                    // First upload the image
                    StorageReference filePath = mFirebaseStorage.child("post_images").child(mUri.getLastPathSegment());

                    filePath.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Get the post image download url
                            final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(PostActivity.this, "Successfully uploaded the image", Toast.LENGTH_SHORT).show();
                            final DatabaseReference newPost = mDatabaseRef.push();

                            //Add post contents to database reference
                            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    newPost.child("title").setValue(postTitle);
                                    newPost.child("desc").setValue(postDesc);
                                    newPost.child("imageUrl").setValue(downloadUrl.toString());
                                    newPost.child("uid").setValue(mCurrentUser.getUid());
                                    newPost.child("username").setValue(dataSnapshot.child("name").getValue())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        finish();
                                                    }
                                                }
                                            });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });

                        }
                    });

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            mUri = data.getData();
            Log.d(TAG, "onActivityResult: " + mUri.getLastPathSegment().toString());
            mImageBtnAddImage.setImageURI(mUri);
        }
    }
}
