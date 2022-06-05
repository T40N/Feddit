package com.example.fedditandroid;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentViewHolder extends RecyclerView.ViewHolder{
    TextView author;
    TextView comment;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        author = (TextView) itemView.findViewById(R.id.CommentAuthorID);
        comment = (TextView) itemView.findViewById(R.id.CommentTextID);
    }
}
