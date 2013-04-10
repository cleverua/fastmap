package com.cleverua.fastmap.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import com.cleverua.fastmap.models.Model;

import java.util.List;

/**
 * Created by: Alex Kulakovsky
 * Date: 4/9/13
 * Time: 12:26 PM
 * Email: akulakovsky@cleverua.com
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "models.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MODEL = "models";

    private static final String TABLE_MODEL_CREATE =
            "CREATE TABLE " + TABLE_MODEL +
                    " (_id INTEGER PRIMARY KEY, "
                    + Model.KEY_TITLE + " TEXT, "
                    + Model.KEY_LAT + " REAL, "
                    + Model.KEY_LNG + " REAL);";

    private static final String TABLE_MODEL_DROP = "DROP TABLE IF EXISTS " + TABLE_MODEL;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_MODEL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL(TABLE_MODEL_DROP);
    }

    public static void insertToDB(Context context, String tableName, List<ContentValues> valuesList){
        SQLiteDatabase sqlDB = new DBHelper(context).getWritableDatabase();
        sqlDB.beginTransaction();
        try {
            for (ContentValues value: valuesList){
                try {
                    long newID = sqlDB.insertOrThrow(tableName, null, value);
                    if (newID <= 0) {
                        throw new SQLException("Failed to insert row into values_table!");
                    }
                } catch (SQLiteConstraintException e){
                    //Log.e("DB", "Already inserted!!!");
                }
            }
            Log.d("DB", "Values inserted!");
            sqlDB.setTransactionSuccessful();
        } finally {
            sqlDB.endTransaction();
        }
        sqlDB.close();
    }

    public static void clearTable(Context context, String tableName){
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }
}
