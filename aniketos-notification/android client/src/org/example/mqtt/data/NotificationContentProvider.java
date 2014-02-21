/* much of this code is based on the tutorial at
 * http://www.vogella.com/articles/AndroidSQLite/article.html
 * 
 * Kudos to the guys that made that tutorial available!
 */

package org.example.mqtt.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class NotificationContentProvider extends ContentProvider {

	private static final String TAG = "NotificationContentProvider";
	
	 // database
	  private NotificationData database;
	  
	  // used for the UriMacher
	  private static final int NOTIFICATIONS = 10;
	  private static final int NOTIFICATION_ID = 20;
	  
	  private static final String AUTHORITY = "org.example.mqtt.contentprovider";

	  private static final String BASE_PATH = "notifications";
	  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
	      + "/" + BASE_PATH);

	  public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
	      + "/notifications";
	  public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
	      + "/notification";
	  
	  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	  static {
	    sURIMatcher.addURI(AUTHORITY, BASE_PATH, NOTIFICATIONS);
	    sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTIFICATION_ID);
	  }

	  
	@Override
	public boolean onCreate() {
		database = new NotificationData(getContext());
		return false;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    switch (uriType) {
	    case NOTIFICATIONS:
	      rowsDeleted = sqlDB.delete(NotificationData.TABLE_NAME, selection,
	          selectionArgs);
	      break;
	    case NOTIFICATION_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsDeleted = sqlDB.delete(NotificationData.TABLE_NAME,
	        		NotificationData._ID + "=" + id, 
	            null);
	      } else {
	        rowsDeleted = sqlDB.delete(NotificationData.TABLE_NAME,
	        	NotificationData._ID + "=" + id 
	            + " and " + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    long id = 0;
	    switch (uriType) {
	    case NOTIFICATIONS:
	      id = sqlDB.insert(NotificationData.TABLE_NAME, null, values);
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
	}



	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		   // Uisng SQLiteQueryBuilder instead of query() method
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

	    // check if the caller has requested a column which does not exists
	    NotificationData.checkColumns(projection);

	    // Set the table
	    queryBuilder.setTables(NotificationData.TABLE_NAME);

	    int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case NOTIFICATIONS:
	      break;
	    case NOTIFICATION_ID:
	      // adding the ID to the original query
	      queryBuilder.appendWhere(NotificationData._ID + "="
	          + uri.getLastPathSegment());
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }

	    SQLiteDatabase db = database.getWritableDatabase();
	    Cursor cursor = queryBuilder.query(db, projection, selection,
	        selectionArgs, null, null, sortOrder);
	    // make sure that potential listeners are getting notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);

	    return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsUpdated = 0;
	    switch (uriType) {
	    case NOTIFICATIONS:
	      rowsUpdated = sqlDB.update(NotificationData.TABLE_NAME, 
	          values, 
	          selection,
	          selectionArgs);
	      break;
	    case NOTIFICATION_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsUpdated = sqlDB.update(NotificationData.TABLE_NAME, 
	            values,
	            NotificationData._ID + "=" + id, 
	            null);
	      } else {
	        rowsUpdated = sqlDB.update(NotificationData.TABLE_NAME, 
	            values,
	            NotificationData._ID + "=" + id 
	            + " and " 
	            + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	}

}
