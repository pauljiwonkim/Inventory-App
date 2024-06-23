package com.example.paulkiminventoryapp.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.paulkiminventoryapp.model.Categories;

public class InventoryDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int VERSION = 1;
    private Context context;
    public InventoryDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class CategoryTable{
        private static final String TABLE = "Category";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
    }


    // TODO ADD ITEM DESC, QUANTITY, AND PRICE TOO
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
        db.execSQL("create table " + InventoryTable.TABLE + " (" +
                InventoryTable.COL_ID + " integer primary key autoincrement, " +
                InventoryTable.COL_NAME + " text, " +
                InventoryTable.COL_CATEGORY + " text, " +
                InventoryTable.COL_DESCRIPTION + " text, " +
                InventoryTable.COL_QUANTITY + " integer, " +
                InventoryTable.COL_PRICE + " real);");

        db.execSQL("create table " + CategoryTable.TABLE + " (" +
                CategoryTable.COL_ID + " integer primary key autoincrement, " +
                CategoryTable.COL_NAME + " text);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("drop table if exists " + InventoryTable.TABLE);
        onCreate(db);
    }


    //Categories
    public void addCategoryData(Categories category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryTable.COL_NAME, category.getText());
        db.insert(CategoryTable.TABLE, null, values);
        db.close();
    }

    public Cursor readAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + CategoryTable.TABLE, null);
    }

// Items
    public void addItemData(String name,  String description, long quantity, double price) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryTable.COL_NAME, name);
        values.put(InventoryTable.COL_DESCRIPTION, description);
        values.put(InventoryTable.COL_QUANTITY, quantity);
        values.put(InventoryTable.COL_PRICE, price);
        long result = db.insert(InventoryTable.TABLE, null, values);
        if (result == -1) {
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Item Added Successfully!", Toast.LENGTH_SHORT).show();
        }

    }

    public Cursor readItemsByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + InventoryTable.TABLE + " WHERE " + InventoryTable.COL_CATEGORY + "=?", new String[]{category});
    }

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

