package com.example.fedditandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public String idOfPost;
    public String idOfAuthor;
    public int numberOfLikes;

    TextView title, text, likeCounter, author;

    Button postDetailsButton, likeButton;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.TitleValueID);
        text = (TextView) itemView.findViewById(R.id.TextValueID);
        likeCounter = (TextView) itemView.findViewById(R.id.LikeCounterID);
        author = (TextView) itemView.findViewById(R.id.AuthorID);

        postDetailsButton = (Button) itemView.findViewById(R.id.PostButtonID);
        likeButton = (Button) itemView.findViewById(R.id.LikeButtonID);

        postDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PostInfo.class);
                Bundle b = new Bundle();
                b.putString("idOfPost", idOfPost); //Your id
                b.putString("likeCounter", String.valueOf(numberOfLikes));
                b.putString("titleOfPost", title.getText().toString());
                b.putString("textOfPost", text.getText().toString());
                b.putString("idOfAuthor", idOfAuthor);
                intent.putExtras(b); //Put your id to your next Intent
                view.getContext().startActivity(intent);
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(uId.equals(idOfAuthor)){
                    Toast.makeText(view.getContext(), "You are author of the post, you can't like it!", Toast.LENGTH_LONG).show();
                }else{
                    FirebaseFirestore.getInstance()
                            .collection("Posts").document(idOfPost)
                            .collection("Likes").document(uId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()){
                                            Toast.makeText(view.getContext(), "You already liked it!", Toast.LENGTH_LONG).show();
                                        }else{
                                            Like like = new Like(true);
                                            FirebaseFirestore.getInstance()
                                                    .collection("Posts").document(idOfPost)
                                                    .collection("Likes").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .set(like)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            FirebaseFirestore.getInstance().collection("Posts")
                                                                    .document(idOfPost)
                                                                    .update("likeCounter", numberOfLikes + 1)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Toast.makeText(view.getContext(), "Liked!", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(view.getContext(), "Something is wrong with counter!", Toast.LENGTH_LONG).show();
                                                                        }
                                                            });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(view.getContext(), "Something went wrong with adding a like!", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        }
                                    }else{
                                        Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

            }
        });
    }
}
