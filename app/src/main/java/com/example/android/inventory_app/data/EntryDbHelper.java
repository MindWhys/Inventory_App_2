package com.example.android.inventory_app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory_app.data.InventoryContract.InventoryTable;

public class EntryDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = EntryDbHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.

    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + InventoryContract.InventoryTable.TABLE_NAME;

    public EntryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ENTRIES = "CREATE TABLE " + InventoryTable.TABLE_NAME + " ("
                + InventoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryTable.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryTable.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryTable.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryTable.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + InventoryTable.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Tis command is for when ou want to update the database to a new version,
        // e.g. containing a new column, after deleting the old one
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate((db));
    }
}
