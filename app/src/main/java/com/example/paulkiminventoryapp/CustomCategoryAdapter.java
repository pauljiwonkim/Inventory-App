package com.example.paulkiminventoryapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.repo.InventoryRepository;

import java.util.List;

public class CustomCategoryAdapter extends RecyclerView.Adapter<CustomCategoryAdapter.CategoryHolder> {

    private List<Categories> mCategoryList;
    private final int[] mCategoryColors;
    private final Context mContext;
    private final CategoryAdapterListener mListener;
    private int mSelectedCategoryPosition = RecyclerView.NO_POSITION;
    InventoryRepository mInventoryRepository;

    public interface CategoryAdapterListener {
        void onCategorySelected(Categories category);
        void onCategoryLongSelected(Categories category, int position);
    }

    public CustomCategoryAdapter(List<Categories> categoryList, int[] categoryColors, Context context, CategoryAdapterListener listener,InventoryRepository repo) {
        mCategoryList = categoryList;
        mCategoryColors = categoryColors;
        mContext = context;
        mListener = listener;
        mInventoryRepository = repo;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        return new CategoryHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int position) {
        Categories category = mCategoryList.get(position);
        holder.bind(category, position);
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    public void setCategoryList(List<Categories> categoryList) {
        mCategoryList = categoryList;
    }

    public void addCategory(Categories category) {
        mCategoryList.add(category);
        notifyDataSetChanged();
    }

    public void deleteCategory(Categories category) {
        int index = mCategoryList.indexOf(category);
        if (index >= 0) {
            mCategoryList.remove(index);
            if (mInventoryRepository != null) {
                mInventoryRepository.deleteCategory(category);
            }
            notifyItemRemoved(index);
        }
    }

    public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private Categories mCategory;
        private final TextView mCategoryTextView;

        public CategoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.category_view_items, parent, false));
            mCategoryTextView = itemView.findViewById(R.id.category_text_view);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Categories category, int position) {
            mCategory = category;
            mCategoryTextView.setText(category.getText());
            if (mSelectedCategoryPosition == position) {
                mCategoryTextView.setBackgroundColor(Color.RED);
            } else {
                int colorIndex = category.getText().length() % mCategoryColors.length;
                mCategoryTextView.setBackgroundColor(mCategoryColors[colorIndex]);
            }
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onCategorySelected(mCategory);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mListener != null) {
                mSelectedCategoryPosition = getAdapterPosition();
                mListener.onCategoryLongSelected(mCategory, mSelectedCategoryPosition);
                notifyItemChanged(mSelectedCategoryPosition);
            }
            return true;
        }
    }
}
