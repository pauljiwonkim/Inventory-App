package com.example.paulkiminventoryapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import com.example.paulkiminventoryapp.model.Items;
import androidx.lifecycle.ViewModelProvider;
import com.example.paulkiminventoryapp.viewmodel.InventoryDetailViewModel;


public class InventoryEditActivity extends AppCompatActivity {

    private InventoryDetailViewModel mInventoryDetailViewModel;
    public static final String EXTRA_ITEM_ID = "com.example.paulkiminventoryapp.item_id";
    public static final String EXTRA_CATEGORY_ID = "com.example.paulkiminventoryapp.category_id";

    private EditText mItemEditText;
    private long mItemId;
    private Items mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_edit);

        mItemEditText = findViewById(R.id.item_edit_text);

        findViewById(R.id.save_button).setOnClickListener(view -> saveButtonClick());

        mInventoryDetailViewModel = new ViewModelProvider(this).get(InventoryDetailViewModel.class);

        // Get question ID from QuestionActivity
        Intent intent = getIntent();
        mItemId = intent.getLongExtra(EXTRA_ITEM_ID, -1);

        if (mItemId == -1) {
            // Add new question
            mItem = new Items();
            mItem.setCategoryId(intent.getLongExtra(EXTRA_CATEGORY_ID, 0));

            setTitle(R.string.add_item);
        }
        else {
            // Display existing question from ViewModel
            mInventoryDetailViewModel.loadItem(mItemId);
            mInventoryDetailViewModel.itemsLiveData.observe(this, item -> {
                mItem = item;
                updateUI();
            });

            // Set title
                setTitle(R.string.edit_item);
            }
        }


    private void updateUI() {
        mItemEditText.setText(mItem.getText());
    }

    private void saveButtonClick() {
        mItem.setText(mItemEditText.getText().toString());

        // Save new or existing question
        if (mItemId == -1) {
            mInventoryDetailViewModel.addItem(mItem);
        }
        else {
            mInventoryDetailViewModel.updateItem(mItem);
        }

        // Send back OK result
        setResult(RESULT_OK);
        finish();
    }
}