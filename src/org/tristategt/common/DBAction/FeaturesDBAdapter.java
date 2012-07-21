package org.tristategt.common.DBAction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FeaturesDBAdapter {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LONG = "long";
	public static final String KEY_NOTE = "note";
	public static final String KEY_GEOM = "geometry";
	public static final String KEY_VALUES = "_values";
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "GIS_Features";
	private static final String DATABASE_TABLE = "field_notes";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE =
			"create table " + DATABASE_TABLE + "(" + KEY_ROWID +" integer primary key autoincrement, " 
					+ KEY_LAT + " real not null, " + KEY_LONG + " real not null, " + KEY_NOTE + " text not null, " 
					+ KEY_GEOM + " text not null, " + KEY_VALUES + " text not null);";
	private Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public FeaturesDBAdapter(Context ctx)
	{
		this.context = ctx;
		this.DBHelper = new DatabaseHelper(context);
	}

	public FeaturesDBAdapter open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		DBHelper.close();
	}
	
	//---insert a note into the database---
	public long insertGraphic(Double lat, Double _long, String note, String geom, String values)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LAT, lat);
		initialValues.put(KEY_LONG, _long);
		initialValues.put(KEY_NOTE, note);
		initialValues.put(KEY_GEOM, geom);
		initialValues.put(KEY_VALUES, values);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	
	//---deletes a particular note---
	public boolean deleteGraphic(long rowId)
	{
		return db.delete(DATABASE_TABLE, KEY_ROWID + " = " + rowId, null) > 0;
	}
	
	//---retrieves all the titles---
	public Cursor getAllGraphics()
	{
		return db.query(DATABASE_TABLE, new String[] {
				KEY_ROWID,
				KEY_LAT,
				KEY_LONG,
				KEY_NOTE,
				KEY_GEOM,
				KEY_VALUES
			},
		null,
		null,
		null,
		null,
		null);
	}
	
	//---retrieves a particular note---
	public Cursor getGraphic(long rowId) throws SQLException
	{
		Cursor mCursor =
				db.query(true, DATABASE_TABLE, new String[] {
						KEY_ROWID,
						KEY_LAT,
						KEY_LONG,
						KEY_NOTE,
						KEY_GEOM,
						KEY_VALUES
				},
				KEY_ROWID + "=" + rowId,
				null,
				null,
				null,
				null,
				null);
		if (mCursor != null) 
			mCursor.moveToFirst();
		
		return mCursor;
	}
	
	//---updates a note---
	public boolean updateGraphic(long rowId, Double lat, Double _long, String note, String geom, String values)
	{
		ContentValues args = new ContentValues();
		args.put(KEY_LAT, lat);
		args.put(KEY_LONG, _long);
		args.put(KEY_NOTE, note);
		args.put(KEY_GEOM, geom);
		args.put(KEY_VALUES, values);
		return db.update(DATABASE_TABLE, args,
				KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	//---Delete Database---
	public boolean dropDB()
	{
		context.deleteDatabase(DATABASE_NAME);
		return true;
	}
		
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE);
            onCreate(db);
        }
	}
}
