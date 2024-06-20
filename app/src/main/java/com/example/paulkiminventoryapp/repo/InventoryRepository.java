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

        // Add inventory items to each category
        Items item1 = new Items("Items Item 1");
        item1.setId(1);
        item1.setCategoryId(1); // Link to Category 1
        addItem(item1);

        Items item2 = new Items("Items Item 2");
        item2.setId(2);
        item2.setCategoryId(2); // Link to Category 2
        addItem(item2);

        Items item3 = new Items("Items Item 3");
        item3.setId(3);
        item3.setCategoryId(3); // Link to Category 3
        addItem(item3);
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
                    currentItem.setText(updatedItem.getText());
                    // Replace the item in the list
                    itemList.set(i, currentItem);
                    break;
                }
            }
        }
    }

    public LiveData<Items> getItem(long itemId) {
        MutableLiveData<Items> liveData = new MutableLiveData<>();
        for (List<Items> items : mInventoryMap.values()) {
            for (Items item : items) {
                if (item.getId() == itemId) {
                    liveData.setValue(item);
                    return liveData;
                }
            }
        }
        return null;
    }

    public List<Items> getItems(long categoryId) {
        return mInventoryMap.get(categoryId);
    }
}
