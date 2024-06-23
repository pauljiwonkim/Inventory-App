package com.example.paulkiminventoryapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomLoginAdapter extends RecyclerView.Adapter<CustomLoginAdapter.ViewHolder> {

    public Context context;
    public ArrayList <String> user_id, user_name, user_password, user_email;

    public CustomLoginAdapter(Context context,
                              ArrayList <String> user_id,
                              ArrayList <String> user_name,
                              ArrayList <String> user_password,
                              ArrayList <String> user_email){

        this.context = context;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_password = user_password;
        this.user_email = user_email;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.login_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.user_id_txt.setText(user_id.get(position).toString());
        holder.user_name_txt.setText(user_name.get(position).toString());
        holder.user_password_txt.setText(user_password.get(position).toString());
        holder.user_email_txt.setText(user_email.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return user_id.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView user_id_txt, user_name_txt, user_password_txt, user_email_txt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_id_txt = itemView.findViewById(R.id.user_id_text);
            user_name_txt = itemView.findViewById(R.id.user_name_text);
            user_password_txt = itemView.findViewById(R.id.user_password_text);
            user_email_txt = itemView.findViewById(R.id.user_email_text);

        }
    }
}
