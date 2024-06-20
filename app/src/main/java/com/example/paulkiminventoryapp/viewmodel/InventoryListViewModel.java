package com.example.paulkiminventoryapp.viewmodel;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.AndroidViewModel;
import androidx.annotation.NonNull;
import com.example.paulkiminventoryapp.model.Items;
import com.example.paulkiminventoryapp.repo.InventoryRepository;
import java.util.List;
public class InventoryListViewModel extends AndroidViewModel {
    private InventoryRepository inventoryRepository;
    private final MutableLiveData<Long> mCategoryIdLiveData = new MutableLiveData<>();


    public InventoryListViewModel(@NonNull Application application) {
        super(application);
        inventoryRepository = InventoryRepository.getInstance(application.getApplicationContext());
    }

    public List<Items> getItems(long itemId) {
        return inventoryRepository.getItems(itemId);
    }


    public void deleteItem(Items items) {
        inventoryRepository.deleteItem(items);
    }
}