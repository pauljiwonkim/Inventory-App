package com.example.paulkiminventoryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.repo.InventoryRepository;

import java.util.List;

public class CustomCategoryAdapter extends RecyclerView.Adapter<CustomCategoryAdapter.CategoryHolder> {

    private List<Categories> mCategoryList;  // List of categories
    private final int[] mCategoryColors;     // Array of colors for category backgrounds
    private final Activity activity;         // Activity reference

    private final Context context;           // Context for resource access
    private final CategoryAdapterListener mListener; // Listener for category interactions
    private int selectedCategoryPosition = RecyclerView.NO_POSITION; // Position of the selected category
    private final InventoryRepository mInventoryRepository; // Repository
    boolean categorySelected = false;


    // Interface for handling category selection and long-click events
    public interface CategoryAdapterListener {
        void onCategoryPressed(Categories category);
        void onCategoryLongSelected(Categories category, int position, boolean categorySelected);
    }

    // Constructor
    public CustomCategoryAdapter(List<Categories> categoryList, int[] categoryColors, Activity activity,Context context, CategoryAdapterListener listener, InventoryRepository repo) {
        mCategoryList = categoryList;
        mCategoryColors = categoryColors;
        this.activity = activity;
        this.context = context;
        this.mListener = listener;
        mInventoryRepository = repo;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate category view layout
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new CategoryHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int position) {
        // Bind category data to the view holder
        Categories category = mCategoryList.get(position);
        holder.bind(category, position);

        if (selectedCategoryPosition == position) {
            holder.itemView.setBackgroundColor(Color.RED);
        } else {
            int colorIndex = category.getText().length() % mCategoryColors.length;
            holder.itemView.setBackgroundColor(mCategoryColors[colorIndex]);
        }


        holder.categoryEditLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, InventoryActivity.class);
            intent.putExtra("categorySelected", categorySelected);
            intent.putExtra(InventoryActivity.EXTRA_CATEGORY_ID, category.getId());
            intent.putExtra(InventoryActivity.EXTRA_CATEGORY_TEXT, category.getText());
            activity.startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of items
        return mCategoryList.size();
    }

    // Update the list of categories and notify changes
    public void setCategoryList(List<Categories> categoryList) {
        mCategoryList = categoryList;
        notifyDataSetChanged();
    }

    // Add a new category and notify changes
    public void addCategory(Categories category) {
        mCategoryList.add(category);
        notifyDataSetChanged();

    }

    // Delete a category and notify changes
    public void deleteCategory(Categories category) {
        if (category == null) {
            throw new NullPointerException("Attempting to delete a null category");
        }

        int index = mCategoryList.indexOf(category);
        if (index < 0) {
            throw new IndexOutOfBoundsException("Category not found in the list: " + category);
        }

        try {
            // Remove category from the list
            mCategoryList.remove(index);
            notifyItemRemoved(index);

            // Delete category from the database
            if (mInventoryRepository != null) {
                mInventoryRepository.deleteCategory(category);
            }

            // Reset selection
            selectedCategoryPosition = RecyclerView.NO_POSITION;
            categorySelected = false; // Ensure selection state is false

            // Notify listener
            if (mListener != null) {
                mListener.onCategoryLongSelected(null, RecyclerView.NO_POSITION, false);
            }

        } catch (SQLiteException e) {
            // Revert the list change if an error occurs
            mCategoryList.add(index, category);
            notifyItemInserted(index);
            throw new SQLiteException("Failed to delete category from database", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while deleting category", e);
        }
    }


    public int getSelectedCategoryPosition() {
        return selectedCategoryPosition;
    }
    public void setSelectedCategoryPosition(int position) {
        this.selectedCategoryPosition = position;
    }

    public void resetSelection() {
        if (selectedCategoryPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(selectedCategoryPosition); // Update previously selected item
        }
        selectedCategoryPosition = RecyclerView.NO_POSITION; // Clear selection
        categorySelected = false; // Make sure selection state is false
        notifyDataSetChanged(); // Notify the adapter to refresh the entire list
    }

    public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private Categories mCategory;
        private final TextView mCategoryTextView;
        final CardView categoryEditLayout;

        public CategoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.category_view_items, parent, false));
            mCategoryTextView = itemView.findViewById(R.id.category_text_view);
            categoryEditLayout = itemView.findViewById(R.id.category_card_view);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Categories category, int position) {
            // Bind category data to the view
            mCategory = category;
            mCategoryTextView.setText(category.getText());
        }

        @Override
        public void onClick(View view) {
            // Handle click event and notify listener
            if (mListener != null && mCategory != null) {
                if (mCategory.getId() != null) {
                    mListener.onCategoryPressed(mCategory);
                } else {
                    Toast.makeText(context, "Category Id is null", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mListener != null && mCategory != null) {
                // Handle long-click event and notify listener
                selectedCategoryPosition = getAdapterPosition();
                categorySelected = true;
                mListener.onCategoryLongSelected(mCategory, selectedCategoryPosition, true);
                notifyItemChanged(selectedCategoryPosition);
                return true;
            }
            return false;
        }
    }

}

