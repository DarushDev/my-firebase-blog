package com.example.myfirebaseblog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize recyclerview and Firebase objects
        mRecyclerView = findViewById(R.id.recyclerview_main);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("blog");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        Query blogsQuery = mDatabase.orderByKey();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions
                .Builder()
                .setQuery(blogsQuery, Blog.class)
                .build();

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(options) {

            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_items, parent, false);
                return new BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(BlogViewHolder viewHolder, int position, Blog model) {
                final String post_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImageUrl(getApplicationContext(), model.getImageUrl());
                viewHolder.setUserName(model.getUsername());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleActivity = new Intent(MainActivity.this, SinglePostActivity.class);
                        singleActivity.putExtra("PostId", post_key);
                        startActivity(singleActivity);
                    }
                });
            }

        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        } else if (id == R.id.action_logout) {
            mAuth.signOut();
            Intent logoutIntent = new Intent(MainActivity.this, RegisterActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logoutIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView post_title = mView.findViewById(R.id.text_post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc) {
            TextView post_desc = mView.findViewById(R.id.edittext_post_description);
            post_desc.setText(desc);
        }

        public void setImageUrl(Context context, String imageUrl) {
            ImageView post_image = mView.findViewById(R.id.image_post);
            Picasso.with(context).load(imageUrl).into(post_image);
        }

        public void setUserName(String userName) {
            TextView postUserName = mView.findViewById(R.id.text_post_user);
            postUserName.setText(userName);
        }

    }
}
