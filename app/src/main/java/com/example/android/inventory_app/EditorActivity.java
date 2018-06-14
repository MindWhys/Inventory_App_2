package com.example.android.inventory_app;


import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.inventory_app.data.InventoryContract.InventoryTable;

import org.w3c.dom.Text;


/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the pet data loader */
    private static final int EXISTING_ITEM_LOADER = 0;

    /** Content URI for the existing item (null if it's a new item) */
    private Uri mCurrentItemUri;

    /** EditText field to enter the product's name */
    private EditText productNameEditText;

    /** EditText field to enter the product's price */
    private EditText productPriceEditText;

    /** Button to increase the number of products */
    private Button buyButton;

    /** Button to decrease the number of products */
    private Button sellButton;

    /** EditText field to enter the quantity of products */
    private EditText productQuantityEditText;

    /** EditText field to enter the supplier's name */
    private EditText supplierNameEditText;

    /** EditText field to enter the supplier's phone number */
    private EditText supplierPhoneNumberEditText;

    /** Boolean flag that keeps track of whether the item has been edited (true) or not (false) */
    private boolean mItemHasChanged = false;

    public int number = 1;

    ImageButton supplierCallButton;
    private static final int REQUEST_CALL = 1;


    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mItemHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch the activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // If the intent DOES NOT contain a item content URI, then we know that we are
        // creating a new item.
        if (mCurrentItemUri == null) {
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.editor_activity_title_add_item));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing item, so change the app bar to say "Edit Pet"
            setTitle(getString(R.string.editor_activity_title_edit_item));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all the relevant views that we will need to read user input from
        productNameEditText = findViewById(R.id.edit_product_name);
        productPriceEditText = findViewById(R.id.edit_price);

        buyButton = findViewById(R.id.buy);
        productQuantityEditText = findViewById(R.id.edit_quantity);
        sellButton = findViewById(R.id.edit_sell);

        supplierNameEditText = findViewById(R.id.edit_supplier_name);
        supplierPhoneNumberEditText = findViewById(R.id.edit_supplier_phone_number);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them.  This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        productNameEditText.setOnTouchListener(mTouchListener);
        productPriceEditText.setOnTouchListener(mTouchListener);

        buyButton.setOnTouchListener(mTouchListener);
        productQuantityEditText.setOnTouchListener(mTouchListener);
        sellButton.setOnTouchListener(mTouchListener);

        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

        buyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                productQuantityEditText.setText("" + number++);
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number >= 1){
                    productQuantityEditText.setText("" + number--);
                } else {
                    Toast.makeText(getApplicationContext(), "No items in stock", Toast.LENGTH_SHORT).show();
                }
            }
        });

        supplierCallButton = findViewById(R.id.phone);
        supplierCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String supplierPhoneNumber = supplierPhoneNumberEditText.getText().toString();
                        Intent supplierCallIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + supplierPhoneNumber));
                if (ContextCompat.checkSelfPermission(EditorActivity.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    android.support.v4.app.ActivityCompat.requestPermissions
                            (EditorActivity.this, new String[]{
                                    Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                } else {
                    startActivity(supplierCallIntent);
                }
            }
        });
    }

    /**
     * Get user input from the editor and save pet into the database.
     */
    private void saveItem() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String productNameString = productNameEditText.getText().toString().trim();
        String productPriceString = productPriceEditText.getText().toString().trim();
        String productQuantityString = productQuantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = supplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new item
        // and check if all the fields in the editor are blank
        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(productPriceString) &&
                TextUtils.isEmpty(productQuantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneNumberString)) {
            return;
        }

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(InventoryTable.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryTable.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryTable.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(productPriceString)){
            price = Integer.parseInt(productPriceString);
        }
        values.put(InventoryTable.COLUMN_PRODUCT_PRICE, price);

        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(productPriceString)){
            quantity = Integer.parseInt(productQuantityString);
        }
        values.put(InventoryTable.COLUMN_PRODUCT_QUANTITY, quantity);

        // Determine if this is a new or existing item by checking if mCurrentItemUri is null or not
        if (mCurrentItemUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(InventoryTable.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and e can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues.  Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_item_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), to that they
     * menu can be updated (some items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save an item to database
                saveItem();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user that have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This mehtod is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if(!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                InventoryTable._ID,
                InventoryTable.COLUMN_PRODUCT_NAME,
                InventoryTable.COLUMN_PRODUCT_PRICE,
                InventoryTable.COLUMN_PRODUCT_QUANTITY,
                InventoryTable.COLUMN_SUPPLIER_NAME,
                InventoryTable.COLUMN_SUPPLIER_PHONE_NUMBER };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier_name = cursor.getString(supplierNameColumnIndex);
            String supplier_number = cursor.getString(supplierNumberColumnIndex);

            // Update the views on the screen with the values from the database
            productNameEditText.setText(name);
            productPriceEditText.setText(Integer.toString(price));
            productQuantityEditText.setText(Integer.toString(quantity));
            supplierNameEditText.setText(supplier_name);
            supplierPhoneNumberEditText.setText(supplier_number);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        productNameEditText.setText("");
        productPriceEditText.setText("");
        productQuantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneNumberEditText.setText("");

    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep Editing button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
            dialog.dismiss();
                }
            }
        });

        // Create and show AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this item.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User click on the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked on the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     *  Perform the delete if this is the existing pet.
     */
    private void deletePet() {
        if (mCurrentItemUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful), Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

}
