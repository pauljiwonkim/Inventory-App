package com.example.paulkiminventoryapp;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomItemAdapter extends RecyclerView.Adapter<CustomItemAdapter.ViewHolder> {

    public Context context;
    Activity activity;
    public ArrayList <String> item_id, item_name, item_desc, item_quantity, item_price;
    public CustomItemAdapter(Activity activity, Context context,
                             ArrayList <String> item_id,
                             ArrayList <String> item_name,
                             ArrayList <String> item_desc,
                             ArrayList <String> item_quantity,
                             ArrayList <String> item_price){
        this.activity = activity;
        this.context = context;
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_desc = item_desc;
        this.item_quantity = item_quantity;
        this.item_price = item_price;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.inventory_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.item_id_txt.setText(item_id.get(position));
        holder.item_name_txt.setText(item_name.get(position));
        holder.item_desc_txt.setText(item_desc.get(position));
        holder.item_quantity_txt.setText(item_quantity.get(position));
        holder.item_price_txt.setText(item_price.get(position));

        // Check if item_price has an element at this position
        if (position < item_price.size()) {
            holder.item_price_txt.setText(item_price.get(position));
        } else {
            // Handle the case where there's no price for this item
            holder.item_price_txt.setText(""); // Or some default value
        }
        holder.item_edit_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InventoryEditActivity.class);
                intent.putExtra("id", String.valueOf(item_id.get(position)));
                intent.putExtra("name", String.valueOf(item_name.get(position)));
                intent.putExtra("desc", String.valueOf(item_desc.get(position)));
                intent.putExtra("quantity", String.valueOf(item_quantity.get(position)));
                intent.putExtra("price", String.valueOf(item_price.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });
}

    @Override
    public int getItemCount() {
        return item_id.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_id_txt, item_name_txt, item_desc_txt, item_quantity_txt, item_price_txt;
        LinearLayout item_edit_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_id_txt = itemView.findViewById(R.id.item_id_text_view);
            item_name_txt = itemView.findViewById(R.id.item_name_text_view);
            item_desc_txt = itemView.findViewById(R.id.item_desc_text_view);
            item_quantity_txt = itemView.findViewById(R.id.item_quantity_text_view);
            item_price_txt = itemView.findViewById(R.id.item_price_text_view);
            item_edit_layout = itemView.findViewById(R.id.item_layout);

        }
    }
}
