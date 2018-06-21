package com.example.android.inventory_app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory_app.InventoryProvider;
import com.example.android.inventory_app.data.InventoryContract.InventoryTable;


/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {

    private Uri mCurrentItemUri;

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a lit item view using the layout specified in the List_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.product_name);
        TextView priceTextView = view.findViewById(R.id.product_price);
        TextView quantityTextView = view.findViewById(R.id.product_quantity);



        // Find the columns of the inventory attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_PRODUCT_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_PRODUCT_QUANTITY);

        int price = cursor.getInt(priceColumnIndex);
        String price_text = Integer.toString(price);

        final int quantity = cursor.getInt(quantityColumnIndex);
        String quantity_text = Integer.toString(quantity);

//        // Setting up the SALE button
//        Button saleButton = view.findViewById(R.id.main_sale_button);
//        saleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                int interimQuantity = quantity;
//                int newQuantity = interimQuantity--;
//                if (interimQuantity <= 0) {
//                    Toast.makeText(context, interimQuantity, Toast.LENGTH_SHORT).show();
//                } else {
//                    // Defines an object to contain the updated values
//                    ContentValues values = new ContentValues();
//                    values.put(InventoryTable.COLUMN_PRODUCT_QUANTITY, newQuantity);
//
//                    // Defines selection criteria for the rows you want to update
//                    String mSelectionClause = InventoryTable.COLUMN_PRODUCT_QUANTITY + "=?";
//                    String[] mSelectionArgs = {"1"};
//
//                    /**
//                     *  Sets the updated value and updates the selected words.
//                     */
//                    values.putNull(InventoryTable.COLUMN_PRODUCT_QUANTITY);
//
//                    int mRowsUpdated = context.getContentResolver().update(
//                            InventoryTable.CONTENT_URI,
//                            values,
//                            mSelectionClause,
//                            mSelectionArgs);
//                    Toast.makeText(context, "Row: " + mRowsUpdated + " new quantity is " + newQuantity, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        // Read the inventory attributes from the Cursor for the current pet
        String itemName = cursor.getString(nameColumnIndex);

        // If the item price is empty string or null, then use some default text
        // that says "Free", so the TextView isn't blank.
        if (TextUtils.isEmpty(price_text)) {
            price_text = context.getString(R.string.price_free);
        }

        // If the item quantity is empty string or null, then use some default text
        // that says "No stock", so the TextView isn't blank.
        if (TextUtils.isEmpty(quantity_text)) {
            quantity_text = context.getString(R.string.no_stock);
        }

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(itemName);
        priceTextView.setText(price_text);
        quantityTextView.setText(quantity_text);
    }
}