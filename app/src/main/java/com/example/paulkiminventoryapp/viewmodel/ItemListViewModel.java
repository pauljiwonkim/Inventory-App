package com.example.paulkiminventoryapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.AndroidViewModel;
import androidx.annotation.NonNull;

import com.example.paulkiminventoryapp.model.Items;
import com.example.paulkiminventoryapp.repo.InventoryRepository;

import java.util.List;

public class ItemListViewModel extends AndroidViewModel {
    private final InventoryRepository mInvenRepo;

    public ItemListViewModel(@NonNull Application application) {
        super(application);
        mInvenRepo = InventoryRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Items>> getItems(String categoryId) {
        return mInvenRepo.getItems(categoryId);
    }

    public void addItem(Items item) {
        mInvenRepo.addItem(item);
    }
    public void deleteItem(Items items) {
        mInvenRepo.deleteItem(items);
    }
}