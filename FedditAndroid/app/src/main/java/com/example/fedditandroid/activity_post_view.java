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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class activity_post_view extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirestoreRecyclerOptions<Post> options;
    private FirestoreRecyclerAdapter<Post, PostViewHolder> adapter;
    private RecyclerView recyclerView;
    private CollectionReference dbRef;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        dbRef = FirebaseFirestore.getInstance().collection("Posts");
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = dbRef.orderBy("likeCounter").whereEqualTo("userPostID", uId).limit(50);

        recyclerView = (RecyclerView) findViewById(R.id.PostsRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity_post_view.this));

        options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {
                holder.title.setText(model.getPostTitle());
                holder.text.setText(model.getPostText());
                holder.idOfPost = getSnapshots().getSnapshot(position).getId();
                holder.numberOfLikes = model.getLikeCounter();
                holder.likeCounter.setText(String.valueOf(model.getLikeCounter()));
                holder.idOfAuthor = model.getUserPostID();
                FirebaseFirestore.getInstance().collection("Users").document(model.getUserPostID()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                holder.author.setText(user.fullName);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_view, parent, false);

                return new PostViewHolder(view);
            }

        };

        adapter.startListening();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity_post_view.this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.seperator));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        backBtn = (Button) findViewById(R.id.returnHomeBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_post_view.this, profile_Fragment.class));
            }
        });

        Spinner sortSpinner = (Spinner) findViewById(R.id.sort_spinner_1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Query query = dbRef.orderBy(adapterView.getItemAtPosition(i).toString()).limit(50);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}