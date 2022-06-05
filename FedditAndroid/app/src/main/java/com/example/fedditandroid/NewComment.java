package com.example.fedditandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewComment extends AppCompatActivity {

    String uId, idOfPost;
    EditText addComment;
    Button submit, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);

        addComment = (EditText) findViewById(R.id.CommentTextAddID);
        submit = (Button) findViewById(R.id.SubmitButtonID);
        back = (Button) findViewById(R.id.BackButtonID);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idOfPost = extras.getString("idOfPost");
            uId = extras.getString("uId");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewComment.this, MainPage.class));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentString = addComment.getText().toString().trim();
                if(addComment.length() == 0){
                    addComment.setError("Text of comment is required!");
                    addComment.requestFocus();
                    return;
                }
                FirebaseFirestore.getInstance().collection("Users")
                        .document(uId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                Comment comment = new Comment(commentString, user.fullName, uId);
                                FirebaseFirestore.getInstance().collection("Posts")
                                        .document(idOfPost)
                                        .collection("Comments")
                                        .add(comment)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(NewComment.this, "Comment Added!", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(NewComment.this, MainPage.class));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(NewComment.this, "Something is wrong with your comment!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NewComment.this, "Something is wrong with reading your profile data!", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}