package org.example.mqtt.data;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class NotificationData extends SQLiteOpenHelper {

	  private static final String TAG = "NotificationData";
	
	  private static final String DATABASE_NAME = "mqtt_notif.db";
	  private static final int DATABASE_VERSION = 3;

	  public static final String TABLE_NAME = "notification";

	  public static final String _ID = BaseColumns._ID;
	  public static final String SERVICE_ID = "service_id"; // string
	  public static final String ALERT_TYPE = "alert_type"; // string
	  public static final String DESCRIPTION = "description"; // string
	  public static final String SERVER_TIME = "server_time"; // string - > ISO 8601 time
	  public static final String VALUE = "value"; // string
	  public static final String THREAT_ID = "threat_id"; // string
	  public static final String THRESHOLD = "threshold"; // int
	  public static final String SERVICE_FULL_URI = "service_uri"; // string
	
	  public static final String [] COLUMNS = {_ID,SERVICE_ID,ALERT_TYPE,DESCRIPTION,SERVER_TIME,VALUE,THREAT_ID,THRESHOLD,SERVICE_FULL_URI};
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql =
			    "CREATE TABLE " + TABLE_NAME + " ("
			      + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			      + SERVICE_ID + " TEXT NOT NULL, "
			      + ALERT_TYPE + " TEXT, "
			       +  DESCRIPTION + " TEXT, "
					 + SERVER_TIME+ " BIGINT, "
					 + VALUE+ " TEXT, "
					 + THREAT_ID + " TEXT, "
					 + THRESHOLD + " INTEGER,"
					 + SERVICE_FULL_URI + " TEXT"
			      + ");";
		db.execSQL(sql);

	}
	
	public NotificationData(Context context) {
		  super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version "
		        + oldVersion + " to " + newVersion
		        + ", which will destroy all old data");
		  db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		  onCreate(db);

	}
	
/*
	// TODO: possibly make it assynchronous
	// inserts notification on the db
	public void insert(String service_id, String alert_type, String description,
			String server_time,String value,String threat_id, int threshold) {
		  
		SQLiteDatabase db = getWritableDatabase();

		  ContentValues values = new ContentValues();
		  values.put(SERVICE_ID, service_id);
		  values.put(ALERT_TYPE, alert_type);
		  values.put(DESCRIPTION, description);
		  values.put(SERVER_TIME, server_time);		  
		  values.put(VALUE, value);
		  values.put(THREAT_ID, threat_id);
		  values.put(THRESHOLD, threshold);
		  
		  db.insertOrThrow(TABLE_NAME, null, values);
		}
	
	// TODO: implement the order by
	// pass as input the controlling activity and an array of collumn X value to filter one
	public Cursor all(Activity activity, String[] columns, String[] values ) {
		  String[] from = null; // means all columns 
		  //{ _ID, SERVICE_ID, ALERT_TYPE, DESCRIPTION, SERVER_TIME, VALUE,THREAT_ID,THRESHOLD };
		  String whereClause = null;
		  String[] whereArgs = null;
		  String orderBy = null;
		  
		  // sanity check and possibly setting whereClause and whereArgs
		  if (null != columns && null != values){
			  // check if have same number of items, check if collumns matchers a collumn name
			  if (columns.length != values.length)
				  return null;
			  if(checkColumns(columns) == false )
				  return null;
			  
			  // setting whereClause and whereArgs
			  for(int i=0; i<columns.length; i++){
				  whereClause +=  columns[i] + " = ?";
				  if(i< (columns.length-1) )
					  whereClause += " AND ";
			  }
			  whereArgs = values;
			  
		  }else{
			  // if just one of them is null, we return a null cursor
			  if(!(null == columns && null == values)){
				  Log.d(TAG, "number of collumns mistmacth nb of values when querying");
				  return null;
			  }
		  }
		  
		  // db
		  SQLiteDatabase db = getReadableDatabase();
		  Cursor cursor = db.query(TABLE_NAME, from, whereClause, whereArgs, null, null, orderBy);
		  activity.startManagingCursor(cursor);

		  return cursor;
		}
	*/
		public long count() {
		    SQLiteDatabase db = getReadableDatabase();
		    return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
		}
	
	// check if columns in the array are valid
	public static boolean checkColumns(String[] columns){
		for(int i=0; i<columns.length;i++){
			if ( isValidColumn(columns[i]) == false) 
				return false;
		}
		
		return true;
	}
	
	// check if column is valid
	public static boolean isValidColumn(String column){
		for(int i=0; i<COLUMNS.length;i++){
			if(column.equals(COLUMNS[i]))
				return true;
		}
		
		return false;
	}

}
