package edu.esiea.ecommerce.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_database";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";

    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";

    private static final String TABLE_USER_FAVORITES = "user_favorites";
    private static final String COLUMN_FAVORITE_ID = "favorite_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTableQuery = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY)";

        db.execSQL(createUsersTableQuery);

        // Create products table
        String createProductsTableQuery = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT)";

        db.execSQL(createProductsTableQuery);

        // Create user_favorites junction table
        String createUserFavoritesTableQuery = "CREATE TABLE " + TABLE_USER_FAVORITES + " (" +
                COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_PRODUCT_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + "))";

        db.execSQL(createUserFavoritesTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Method to add a favorite product for a user
    public void addFavoriteProductForUser(long userId, String productName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get product ID
        String[] productColumns = {COLUMN_PRODUCT_ID};
        String productSelection = COLUMN_PRODUCT_NAME + "=?";
        String[] productSelectionArgs = {productName};
        Cursor productCursor = db.query(TABLE_PRODUCTS, productColumns, productSelection, productSelectionArgs, null, null, null);

        long productId = -1; // Default value if not found

        if (productCursor.moveToFirst()) {
            int columnIndex = productCursor.getColumnIndex(COLUMN_PRODUCT_ID);

            // Check if the column index is valid
            if (columnIndex != -1) {
                productId = productCursor.getLong(columnIndex);
            }
        }

        productCursor.close();

        // Only proceed if a valid product ID is obtained
        if (productId != -1) {
            // Add the favorite product for the user in the junction table
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_PRODUCT_ID, productId);

            db.insert(TABLE_USER_FAVORITES, null, values);
        }

        db.close();
    }

    public List<Long> getUserFavoriteProductIds(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Long> favoriteProductIds = new ArrayList<>();

        // Query the user_favorites table to get the list of favorite product IDs for the specified user
        String[] columns = {COLUMN_PRODUCT_ID};
        String selection = COLUMN_USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_USER_FAVORITES, columns, selection, selectionArgs, null, null, null);

        // Iterate through the cursor and add product IDs to the list
        if (cursor.moveToFirst()) {
            do {
                int columnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_ID);

                // Check if the column index is valid
                if (columnIndex != -1) {
                    long productId = cursor.getLong(columnIndex);
                    favoriteProductIds.add(productId);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return favoriteProductIds;
    }
}

