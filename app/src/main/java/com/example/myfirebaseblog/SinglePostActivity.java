package com.example.myfirebaseblog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.example.myfirebaseblog.MainActivity.EXTRA_POST_KEY;

public class SinglePostActivity extends AppCompatActivity {

    private ImageView ivPostImage;
    private TextView tvPostTitle;
    private TextView tvPostDesc;
    String post_key = null;
    private DatabaseReference mDatabase;
    private Button btnDeletePost;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        ivPostImage = findViewById(R.id.iv_singlepost_image);
        tvPostTitle = findViewById(R.id.tv_singlepost_title);
        tvPostDesc = findViewById(R.id.tv_singlepost_desc);
        btnDeletePost = findViewById(R.id.btn_singlepost_delete);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("blog");

        post_key = getIntent().getExtras().getString(EXTRA_POST_KEY);

        btnDeletePost.setVisibility(View.INVISIBLE);
        btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(post_key).removeValue();
                finish();
            }
        });

        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("imageUrl").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                tvPostTitle.setText(post_title);
                tvPostDesc.setText(post_desc);
                Picasso.with(SinglePostActivity.this).load(post_image).into(ivPostImage);
                if (mAuth.getCurrentUser().getUid().equals(post_uid)) {
                    btnDeletePost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
