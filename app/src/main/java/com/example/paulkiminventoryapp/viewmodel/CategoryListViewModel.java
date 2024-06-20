package com.example.paulkiminventoryapp.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.repo.InventoryRepository;
import java.util.List;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
public class CategoryListViewModel extends AndroidViewModel{
    private InventoryRepository mInvenRepo;
    public CategoryListViewModel(Application application) {
        super(application);
        mInvenRepo = InventoryRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Categories>> getCategories() {
        return mInvenRepo.getCategories();
    }


    public void addCategory(Categories category) {
        mInvenRepo.addCategory(category);
    }

    public void deleteCategory(Categories category) {
        mInvenRepo.deleteCategory(category);
    }
}
