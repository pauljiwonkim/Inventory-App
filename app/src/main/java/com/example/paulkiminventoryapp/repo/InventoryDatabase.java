package com.example.paulkiminventoryapp.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.model.Items;

public class InventoryDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int VERSION = 1;
    private final Context context;

    // Constructor
    public InventoryDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    // Category Table
    private static final class CategoryTable{
        private static final String TABLE = "Category";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
    }


    // Items Table
    private static final class InventoryTable {
        private static final String TAG = "InventoryDatabase";
        private static final String TABLE = "Items";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
        private static final String COL_CATEGORY = "category";
        private static final String COL_DESCRIPTION = "description";
        private static final String COL_QUANTITY = "quantity";
        private static final String COL_PRICE = "price";

        // private static final String COL_LAST_UPDATED = "last_updated";
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables

        db.execSQL("create table " + InventoryTable.TABLE + " (" +
                InventoryTable.COL_ID + " integer primary key autoincrement, " +
                InventoryTable.COL_NAME + " text, " +
                InventoryTable.COL_CATEGORY + " text, " +
                InventoryTable.COL_DESCRIPTION + " text, " +
                InventoryTable.COL_QUANTITY + " integer, " +
                InventoryTable.COL_PRICE + " REAL, " +
                "FOREIGN KEY(" + InventoryTable.COL_CATEGORY + ") REFERENCES " + CategoryTable.TABLE + "(" + CategoryTable.COL_ID + "));");

        db.execSQL("create table " + CategoryTable.TABLE + " (" +
                CategoryTable.COL_ID + " integer primary key autoincrement, " +
                CategoryTable.COL_NAME + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("drop table if exists " + InventoryTable.TABLE);
        db.execSQL("drop table if exists " + CategoryTable.TABLE);
        onCreate(db);
    }


    //Categories Functions

    // Adds category to database
    public void addCategoryData(Categories category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryTable.COL_NAME, category.getText());
        long id = db.insert(CategoryTable.TABLE, null, values);
        category.setId(String.valueOf(id));
        db.close();

    }

    // Updates category in database
    public void deleteCategoryData(Categories category) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CategoryTable.TABLE, CategoryTable.COL_ID + " = ?", new String[]{category.getId()});
        db.close();
    }

    // Reads all categories from database
    public Cursor readAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + CategoryTable.TABLE, null);
    }

    // Items Functions
    public void addItemData(Items item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryTable.COL_NAME, item.getItemName());
        values.put(InventoryTable.COL_CATEGORY, item.getCategoryId());
        values.put(InventoryTable.COL_DESCRIPTION, item.getItemDesc());
        values.put(InventoryTable.COL_QUANTITY, item.getItemQuantity());
        values.put(InventoryTable.COL_PRICE, item.getItemPrice());
        long result = db.insert(InventoryTable.TABLE, null, values);
        db.close();
        if (result == -1) {
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Item Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateItemData(Items item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryTable.COL_ID, item.getId());
        values.put(InventoryTable.COL_NAME, item.getItemName());
        values.put(InventoryTable.COL_CATEGORY, item.getCategoryId());
        values.put(InventoryTable.COL_DESCRIPTION, item.getItemDesc());
        values.put(InventoryTable.COL_QUANTITY, item.getItemQuantity());
        values.put(InventoryTable.COL_PRICE, item.getItemPrice());
        Log.d("InventoryDatabase", "Updating item with ID: " + item.getId() + " and CategoryId: " + item.getCategoryId());
        db.update(InventoryTable.TABLE, values, InventoryTable.COL_ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
    }

    // Items Functions

    // Deletes item from database
    public void deleteItemData(Items item) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(InventoryTable.TABLE, InventoryTable.COL_ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
    }

    // Deletes items from database by category
    public void deleteItemsByCategory(String categoryId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(InventoryTable.TABLE, InventoryTable.COL_CATEGORY + " = ?", new String[]{categoryId});
        db.close();
    }

    // Reads items from database by category
    public Cursor readItemsByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { category };
        String query = "SELECT * FROM " + InventoryTable.TABLE +
                " WHERE " + InventoryTable.COL_CATEGORY + " = ? " +
                " ORDER BY " + InventoryTable.COL_NAME + " ASC";
        return db.rawQuery(query, selectionArgs);
    }

    // Reads all items from database using cursor
    public Cursor readAllData(){
        String query = "SELECT * FROM " + InventoryTable.TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

}


