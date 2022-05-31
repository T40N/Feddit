package com.example.fedditandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTask extends AppCompatActivity {

    private FirebaseUser user;

    private String userID;

    ProgressBar progressBar;
    EditText postText, postTitle;
    Button addPostConfirmButton, cancelAddPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        postText = (EditText) findViewById(R.id.postTextID);
        postTitle = (EditText) findViewById(R.id.addPostTitleID);
        addPostConfirmButton = (Button) findViewById(R.id.addPostConfirmButtonID);
        cancelAddPostButton = (Button) findViewById(R.id.cancelAddButtonID);

        progressBar = (ProgressBar) findViewById(R.id.addPostProgressBarID);
        progressBar.setVisibility(View.INVISIBLE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        addPostConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postTextString = postText.getText().toString().trim();
                String postTitleString = postTitle.getText().toString().trim();
                if(postTextString.length() == 0){
                    postText.setError("Text of post is required!");
                    postText.requestFocus();
                    return;
                }

                if(postTitleString.length() == 0){
                    postTitle.setError("Title of post is required!");
                    postTitle.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                String key = FirebaseDatabase.getInstance().getReference("Posts").push().getKey();
                Post post = new Post(postTitleString, postTextString, userID);
                FirebaseDatabase.getInstance().getReference("Posts").child(key).setValue(post)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AddTask.this, "Post has been added successfully!", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.VISIBLE);

                                    startActivity(new Intent(AddTask.this, MainPage.class));
                                }else{
                                    Toast.makeText(AddTask.this, "Something went wrong with adding a post.", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });

        cancelAddPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddTask.this, MainPage.class));
            }
        });

    }
}