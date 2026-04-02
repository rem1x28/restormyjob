package com.example.restor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AppDatabase.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_USERS = "users_table";
    private static final String COL_USER_NAME = "NAME";
    private static final String COL_USER_PASS = "PASSWORD";

    private static final String TABLE_ORDERS = "orders_table";
    private static final String COL_ORDER_ID = "ID";
    private static final String COL_ORDER_USER = "USER_NAME";
    private static final String COL_ORDER_RESTOR = "RESTAURANT_NAME";
    private static final String COL_ORDER_TYPE = "TYPE";
    private static final String COL_ORDER_DETAILS = "DETAILS";
    private static final String COL_ORDER_DATE = "DATE_CREATED";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, PASSWORD TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USER_NAME TEXT, " +
                "RESTAURANT_NAME TEXT, " +
                "TYPE TEXT, " +
                "DETAILS TEXT, " +
                "DATE_CREATED DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    public boolean insertUser(String name, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USER_NAME, name);
        contentValues.put(COL_USER_PASS, password);
        return db.insert(TABLE_USERS, null, contentValues) != -1;
    }

    public boolean checkUserExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE NAME = ?", new String[]{name});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean validateUser(String name, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE NAME = ? AND PASSWORD = ?", new String[]{name, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public boolean updateUserName(String oldName, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USER_NAME, newName);
        return db.update(TABLE_USERS, contentValues, "NAME = ?", new String[]{oldName}) > 0;
    }

    public long insertOrder(String userName, String restaurantName, String type, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ORDER_USER, userName);
        contentValues.put(COL_ORDER_RESTOR, restaurantName);
        contentValues.put(COL_ORDER_TYPE, type);
        contentValues.put(COL_ORDER_DETAILS, details);
        return db.insert(TABLE_ORDERS, null, contentValues);
    }

    public boolean updateOrderDetails(long orderId, String newType, String newDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ORDER_TYPE, newType);
        contentValues.put(COL_ORDER_DETAILS, newDetails);
        return db.update(TABLE_ORDERS, contentValues, "ID = ?", new String[]{String.valueOf(orderId)}) > 0;
    }

    public boolean deleteOrder(int orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ORDERS, "ID = ?", new String[]{String.valueOf(orderId)}) > 0;
    }

    public Cursor getUserOrders(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " WHERE USER_NAME = ? ORDER BY ID DESC", new String[]{userName});
    }
}
