package com.example.paulkiminventoryapp.repo;

import android.content.Context;
import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.model.Items;

import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {

    private static InventoryRepository mInventoryRepo;
    private final InventoryDatabase mInventoryDatabase;

    private InventoryRepository(Context context) {
        mInventoryDatabase = new InventoryDatabase(context);
    }


    public static InventoryRepository getInstance(Context context) {
        if (mInventoryRepo == null) {
            mInventoryRepo = new InventoryRepository(context);
        }
        return mInventoryRepo;
    }

    // Category methods

    // Add a category to the database
    public void addCategory(Categories category) {
        mInventoryDatabase.addCategoryData(category);
    }

    // Delete a category from the database
    public void deleteCategory(Categories category) {
        mInventoryDatabase.deleteCategoryData(category);
        mInventoryDatabase.deleteItemsByCategory(category.getId());
    }


    public LiveData<List <Categories>> getCategories() {
        MutableLiveData<List<Categories>> categoriesLiveData = new MutableLiveData<>();
        Cursor cursor = mInventoryDatabase.readAllCategories();
        List<Categories> categoriesList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Categories category = new Categories(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            category.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
            categoriesList.add(category);
        }
        cursor.close();
        categoriesLiveData.setValue(categoriesList);
        return categoriesLiveData;
    }

    // Item methods

    // Add an item to the database
    public void addItem(Items item) {
        mInventoryDatabase.addItemData(item);
    }

    // Update an item in the database
    public void updateItem(Items updatedItem) {
        mInventoryDatabase.updateItemData(updatedItem);
    }

    // Delete an item from the database
    public void deleteItem(Items item) {
        mInventoryDatabase.deleteItemData(item);
    }
    public LiveData<List<Items>> getItems(String categoryId) {
        MutableLiveData<List<Items>> itemsLiveData = new MutableLiveData<>();
        Cursor cursor = mInventoryDatabase.readItemsByCategory(categoryId);
        List<Items> itemsList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Items item = new Items(
                    cursor.getString(cursor.getColumnIndexOrThrow("_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("quantity")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
            );
            item.setCategoryId(cursor.getString(cursor.getColumnIndexOrThrow("category")));
            itemsList.add(item);
        }
        cursor.close();
        itemsLiveData.setValue(itemsList);
        return itemsLiveData;
    }


    // Get an item from the database
    public LiveData<Items> getItem(String itemId) {
        // Get the item from the database
        MutableLiveData<Items> itemLiveData = new MutableLiveData<>();
        Cursor cursor = mInventoryDatabase.readAllData();

        // Iterate through cursor to find the item with the given ID
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndexOrThrow("_id")).equals(itemId)) {
                Items item = new Items(
                        cursor.getString(cursor.getColumnIndexOrThrow("_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                );
                // Set the category ID and LiveData value of the item
                item.setCategoryId(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                itemLiveData.setValue(item);
                break;
            }
        }
        cursor.close();
        return itemLiveData;
    }
}


