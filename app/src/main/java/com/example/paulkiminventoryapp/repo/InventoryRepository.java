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
    public void addCategory(Categories category) {
        mInventoryDatabase.addCategoryData(category);
    }

    public void deleteCategory(Categories category) {
        mInventoryDatabase.deleteCategoryData(category);
    }


    public LiveData<List<Categories>> getCategories() {
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
    public void addItem(Items item) {
        mInventoryDatabase.addItemData(item);
    }

    public void updateItem(Items updatedItem) {
        mInventoryDatabase.updateItemData(updatedItem);
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

    public LiveData<Items> getItem(String itemId) {
        MutableLiveData<Items> itemLiveData = new MutableLiveData<>();
        Cursor cursor = mInventoryDatabase.readAllData();
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndexOrThrow("_id")).equals(itemId)) {
                Items item = new Items(
                        cursor.getString(cursor.getColumnIndexOrThrow("_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                );
                item.setCategoryId(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                itemLiveData.setValue(item);
                break;
            }
        }
        cursor.close();
        return itemLiveData;
    }
}


