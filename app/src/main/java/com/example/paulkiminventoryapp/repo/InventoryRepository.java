package com.example.paulkiminventoryapp.repo;

import android.content.Context;
import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.model.Items;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class InventoryRepository {

    private static InventoryRepository mInventoryRepo;
    private InventoryDatabase mInventoryDatabase;
    private final List<Categories> mCategoriesList;
    private final HashMap<Long, List<Items>> mInventoryMap;
    private final MutableLiveData<List<Categories>> mCategoriesLiveData;

    private InventoryRepository(Context context) {
        mCategoriesList = new ArrayList<>();
        mInventoryMap = new HashMap<>();
        mCategoriesLiveData = new MutableLiveData<>();

        addStarterData();
        mCategoriesLiveData.setValue(mCategoriesList);
    }

    public static InventoryRepository getInstance(Context context) {
        if (mInventoryRepo == null) {
            mInventoryRepo = new InventoryRepository(context);
        }
        return mInventoryRepo;
    }

    private void addStarterData() {
        // Add a few starting categories
        Categories category1 = new Categories("Category 1");
        category1.setId(1);
        addCategory(category1);

        Categories category2 = new Categories("Category 2");
        category2.setId(2);
        addCategory(category2);

        Categories category3 = new Categories("Category 3");
        category3.setId(3);
        addCategory(category3);

    }

    public void addCategory(Categories category) {
        mCategoriesList.add(category);
        List<Items> itemList = new ArrayList<>();
        mInventoryMap.put(category.getId(), itemList);
        mCategoriesLiveData.setValue(mCategoriesList); // Notify observers
    }

    public void deleteCategory(Categories category) {
        // Remove category from mCategoriesList
        mCategoriesList.remove(category);

        // Remove associated items from mInventoryMap
        mInventoryMap.remove(category.getId());
        mCategoriesLiveData.setValue(mCategoriesList); // Notify observers
    }

    public Categories getCategory(long categoryId) {
        for (Categories category : mCategoriesList) {
            if (category.getId() == categoryId) {
                return category;
            }
        }
        return null;
    }

    public LiveData<List<Categories>> getCategories() {
        return mCategoriesLiveData;
    }

    public void addItem(Items items) {
        List<Items> itemList = mInventoryMap.get(items.getCategoryId());
        if (itemList != null) {
            itemList.add(items);
        }
    }

    public void deleteItem(Items items) {
        List<Items> itemList = mInventoryMap.get(items.getCategoryId());
        if (itemList != null) {
            itemList.remove(items);
        }
    }

    public void updateItem(Items updatedItem) {
        List<Items> itemList = mInventoryMap.get(updatedItem.getCategoryId());
        if (itemList != null) {
            for (int i = 0; i < itemList.size(); i++) {
                Items currentItem = itemList.get(i);
                if (currentItem.getId() == updatedItem.getId()) {
                    // Update item details with the new values
                    currentItem.setItemName(updatedItem.getItemName());
                    currentItem.setItemDesc(updatedItem.getItemDesc());
                    currentItem.setItemQuantity(updatedItem.getItemQuantity());
                    // Replace the item in the list
                    itemList.set(i, currentItem);
                    break;
                }
            }
        }
    }

    public LiveData<List<Items>> getItems(long categoryId) {
        MutableLiveData<List<Items>> itemsLiveData = new MutableLiveData<>();
        List<Items> itemList = mInventoryMap.get(categoryId);
        if (itemList == null) {
            itemList = new ArrayList<>(); // Initialize an empty list if none found
        }
        itemsLiveData.setValue(itemList);
        return itemsLiveData;
    }



    public LiveData<Items> getItem(long itemId) {
        MutableLiveData<Items> itemLiveData = new MutableLiveData<>();
        // Iterate through all categories to find the item
        for (List<Items> itemList : mInventoryMap.values()) {
            for (Items item : itemList) {
                if (item.getId() == itemId) {
                    itemLiveData.setValue(item);
                    return itemLiveData;
                }
            }
        }
        itemLiveData.setValue(null); // Return null if item is not found
        return itemLiveData;
    }



}
