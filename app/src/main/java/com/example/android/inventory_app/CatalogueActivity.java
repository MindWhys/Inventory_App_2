package com.example.android.inventory_app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventory_app.data.InventoryContract.InventoryTable;

/**
 * Displays a list of entries that were entered and stored in the app.
 */

public class CatalogueActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int INVENTORY_LOADER = 0;

    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogueActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView inventoryListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of inventory data in the Cursor.
        // There is no inventory data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(mCursorAdapter);

        // Setup item click listener
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogueActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific pet that was clicked on
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://come.example.android.inventory_app/inventory/2"
                // if the item with ID 2 was clicked on.
                Uri currentItemUri = ContentUris.withAppendedId(InventoryTable.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentItemUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    private void insertItem() {
//        // Gets the data repository in write more
//        SQLiteDatabase db = entryDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(InventoryTable.COLUMN_PRODUCT_NAME, "The Odyssey");
        values.put(InventoryTable.COLUMN_PRODUCT_PRICE, 1299);
        values.put(InventoryTable.COLUMN_PRODUCT_QUANTITY, 10);
        values.put(InventoryTable.COLUMN_SUPPLIER_NAME, "Waterstones");
        values.put(InventoryTable.COLUMN_SUPPLIER_PHONE_NUMBER, "08081188787");

//        // Insert the new row, returning the primary key value in the new row
//        long newRowId = db.insert(InventoryTable.TABLE_NAME, null, values);
//
//        Log.v("CatalogueActivity", "New row ID: " + newRowId);

        //Insert a new row for "The Odyssey" into the provider using the ContentResolver.
        // Use the {@link InventoryTable#CONTENT_URI} to indicate that we want to insert
        // into the inventory database table.
        // Receive the new content URI that will allow us to access "The Odyssey's" data in the future.
        Uri newUri = getContentResolver().insert(InventoryTable.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(InventoryTable.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalogue.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalogue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventoryTable._ID,
                InventoryTable.COLUMN_PRODUCT_NAME,
                InventoryTable.COLUMN_PRODUCT_PRICE,
                InventoryTable.COLUMN_PRODUCT_QUANTITY };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                InventoryTable.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Update {@link InventoryCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called with the data needs ot be deleted
        mCursorAdapter.swapCursor(null);
    }
}
