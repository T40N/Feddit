package com.example.fedditandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostInfo extends AppCompatActivity {

    String idOfPost, idOfAuthor;
    TextView titleOfPost, textOfPost, likesCounter;
    Button like, comment;
    RecyclerView recyclerView;
    private FirestoreRecyclerOptions<Comment> options;
    private FirestoreRecyclerAdapter<Comment, CommentViewHolder> adapter;
    private CollectionReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);

        titleOfPost = (TextView) findViewById(R.id.TitlePostInfoID);
        textOfPost = (TextView) findViewById(R.id.TextPostInfoID);
        likesCounter = (TextView) findViewById(R.id.LikesCounterPostInfoID);

        like = (Button) findViewById(R.id.LikeButtonPostInfoID);
        comment = (Button) findViewById(R.id.CommentButtonPostInfoID);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idOfPost = extras.getString("idOfPost");
            idOfAuthor = extras.getString("idOfAuthor");
            titleOfPost.setText(extras.getString("titleOfPost"));
            textOfPost.setText(extras.getString("textOfPost"));
            likesCounter.setText(extras.getString("likeCounter"));
        }

        dbRef = FirebaseFirestore.getInstance().collection("Posts")
                .document(idOfPost)
                .collection("Comments");
        System.out.println(idOfPost);
        Query query = dbRef.limit(50);

        recyclerView = (RecyclerView) findViewById(R.id.CommentDisplayView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<com.example.fedditandroid.Comment, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull com.example.fedditandroid.Comment model) {
                holder.author.setText(model.author);
                holder.comment.setText(model.comment);
                System.out.println(model.comment);
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_full_info_view, parent, false);

                return new CommentViewHolder(view);
            }
        };

        adapter.startListening();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.seperator));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostInfo.this, NewComment.class);
                Bundle b = new Bundle();
                b.putString("idOfPost", idOfPost); //Your id
                b.putString("uId", uId);
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uId.equals(idOfAuthor)) {
                    Toast.makeText(view.getContext(), "You are author of the post, you can't like it!", Toast.LENGTH_LONG).show();
                } else {
                    FirebaseFirestore.getInstance()
                            .collection("Posts").document(idOfPost)
                            .collection("Likes").document(uId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Toast.makeText(PostInfo.this, "You already liked it!", Toast.LENGTH_LONG).show();
                                        } else {
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
                                                                    .update("likeCounter", Integer.parseInt(likesCounter.getText().toString()) + 1)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Toast.makeText(PostInfo.this, "Liked!", Toast.LENGTH_LONG).show();
                                                                            int numberOfLikes = Integer.parseInt(likesCounter.getText().toString()) + 1;
                                                                            likesCounter.setText(String.valueOf(numberOfLikes));
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(PostInfo.this, "Something is wrong with counter!", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(PostInfo.this, "Something went wrong with adding a like!", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(PostInfo.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}



