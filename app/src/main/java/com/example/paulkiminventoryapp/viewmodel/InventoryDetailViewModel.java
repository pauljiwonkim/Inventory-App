package com.example.paulkiminventoryapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.paulkiminventoryapp.model.Items;
import com.example.paulkiminventoryapp.repo.InventoryRepository;

import java.util.List;

public class InventoryDetailViewModel extends AndroidViewModel {

    private InventoryRepository mInventoryRepo;
    private final MutableLiveData<Long> itemIdLiveData = new MutableLiveData<>();

    public LiveData<Items> itemsLiveData =
            Transformations.switchMap(itemIdLiveData, itemId ->
                mInventoryRepo.getItem(itemId));

    public InventoryDetailViewModel(@NonNull Application application) {
        super(application);
        mInventoryRepo = InventoryRepository.getInstance(application.getApplicationContext());
    }

    public void loadItem(long itemId) {
        itemIdLiveData.setValue(itemId);
    }

    public void addItem(Items items) {
        mInventoryRepo.addItem(items);
    }

    public void updateItem(Items items) {
        mInventoryRepo.updateItem(items);
    }
}