package com.example.paulkiminventoryapp;

import static com.example.paulkiminventoryapp.InventoryActivity.EXTRA_CATEGORY_ID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.model.Items;

import java.util.ArrayList;

public class CustomItemAdapter extends RecyclerView.Adapter<CustomItemAdapter.ItemHolder> {

    private final Context context;
    private final Activity activity;
    private final ArrayList<String> itemId;
    private final ArrayList<String> itemName;
    private final ArrayList<String> itemDesc;
    private final ArrayList<String> itemQuantity;
    private final ArrayList<String> itemPrice;
    private final ItemAdapterListener mListener;
    private int selectedItemPosition = RecyclerView.NO_POSITION;
    boolean isSelected = false;
    private String categoryId;


    public interface ItemAdapterListener {
        void onItemPressed(Items item);
        void onItemLongSelected(Items item, int position, boolean isSelected);
    }

    // Constructor to initialize adapter with necessary data
    public CustomItemAdapter(Activity activity, Context context,
                             ItemAdapterListener listener,
                             String categoryId,
                             ArrayList<String> itemId,
                             ArrayList<String> itemName,
                             ArrayList<String> itemDesc,
                             ArrayList<String> itemQuantity,
                             ArrayList<String> itemPrice) {
        this.activity = activity;
        this.context = context;
        this.mListener = listener;
        this.categoryId = categoryId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.itemQuantity = itemQuantity;
        this.itemPrice = itemPrice;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ItemHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        long quantity = Long.parseLong(itemQuantity.get(position));
        double price = Double.parseDouble(itemPrice.get(position));
        Items item = new Items(itemId.get(position), itemName.get(position), itemDesc.get(position), quantity, price);
        holder.bind(item);

        // Set the background color based on selection status
        if (selectedItemPosition == position && isSelected) {
            holder.itemView.setBackgroundColor(Color.RED);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemEditLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, InventoryEditActivity.class);
            intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
            intent.putExtra("id", itemId.get(position));
            intent.putExtra("name", itemName.get(position));
            intent.putExtra("desc", itemDesc.get(position));
            intent.putExtra("quantity", itemQuantity.get(position));
            intent.putExtra("price", itemPrice.get(position));
            intent.putExtra("isSelected", isSelected);
            activity.startActivityForResult(intent, 1);
        });

    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public void setSelectedItemPosition(int position) {
        this.selectedItemPosition = position;
    }

    @Override
    public int getItemCount() {
        return itemId.size(); // Return the number of items
    }

    // Delete an item from the lists
    public void deleteItem() {
        try {
            if (selectedItemPosition < 0 || selectedItemPosition >= itemId.size()) {
                // Throw an exception if the position is out of bounds
                throw new IndexOutOfBoundsException("Selected item position out of bounds: " + selectedItemPosition);
            }

            // Remove items from lists
            itemId.remove(selectedItemPosition);
            itemName.remove(selectedItemPosition);
            itemDesc.remove(selectedItemPosition);
            itemQuantity.remove(selectedItemPosition);
            itemPrice.remove(selectedItemPosition);
            notifyItemRemoved(selectedItemPosition);

            // Reset selection
            selectedItemPosition = RecyclerView.NO_POSITION;

            // Notify listener about item deletion
            if (mListener != null) {
                mListener.onItemLongSelected(null, RecyclerView.NO_POSITION, false);
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e("CustomItemAdapter", "Error deleting item: " + e.getMessage());
        }
}

    public void resetSelection() {
        if (selectedItemPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(selectedItemPosition); // Update previously selected item
        }
        selectedItemPosition = RecyclerView.NO_POSITION; // Clear selection
        isSelected = false; // Ensure selection state is false
        notifyDataSetChanged(); // Notify the adapter to refresh the entire list
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Items mItem;
        final TextView itemIdTextView;
        final TextView itemNameTextView;
        final TextView itemDescTextView;
        final TextView itemQuantityTextView;
        final TextView itemPriceTextView;
        final LinearLayout itemEditLayout;

        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.inventory_row, parent, false));
            itemIdTextView = itemView.findViewById(R.id.item_id_text_view);
            itemNameTextView = itemView.findViewById(R.id.item_name_text_view);
            itemDescTextView = itemView.findViewById(R.id.item_desc_text_view);
            itemQuantityTextView = itemView.findViewById(R.id.item_quantity_text_view);
            itemPriceTextView = itemView.findViewById(R.id.item_price_text_view);
            itemEditLayout = itemView.findViewById(R.id.item_layout);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Items item) {
            mItem = item;
            itemIdTextView.setText(item.getId());
            itemNameTextView.setText(item.getItemName());
            itemDescTextView.setText(item.getItemDesc());
            itemQuantityTextView.setText(String.valueOf(item.getItemQuantity()));
            itemPriceTextView.setText(String.valueOf(item.getItemPrice()));
        }

        @Override
        public void onClick(View view) {
            if (mListener != null && mItem != null) {
                if (mItem.getId() != null) {
                    mListener.onItemPressed(mItem);
                } else {
                    Toast.makeText(context, "Item is Null", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            selectedItemPosition = getAdapterPosition();
            if (mListener != null && mItem != null) {
                isSelected = true;
                mListener.onItemLongSelected(mItem, selectedItemPosition, true);
                notifyItemChanged(selectedItemPosition);
                return true;
            }
            return false;
        }
    }
}