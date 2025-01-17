package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Models.User;
import com.example.bodify.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewAllUsersAdapter extends RecyclerView.Adapter<ViewAllUsersAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<User> users;

    public ViewAllUsersAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewAllUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAllUsersAdapter.ViewHolder holder, final int position) {
        holder.setUserName(users.get(position).getUserName());
        holder.setEmailAddress(users.get(position).getEmail());
        holder.setImage(users.get(position).getmImageUrl());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView userName, email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            email = itemView.findViewById(R.id.userEmail);
        }

        @SuppressLint("SetTextI18n")
        public void setUserName(String un) {
            userName.setText("User name: " + un);
        }

        @SuppressLint("SetTextI18n")
        public void setEmailAddress(String ea) {
            email.setText("Email: " + ea);
        }

        public void setImage(String i) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(i).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);
            });
        }
    }
}