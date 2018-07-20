/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package exigency.exigency.user.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "android_api";

	// Login table name
	private static final String TABLE_USER = "user";

	// Login Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_UID = "uid";
	private static final String KEY_CREATED_AT = "created_at";
	private static final String KEY_BLOODGROUP = "bloodgroup";
	private static final String KEY_PHONENUMBER = "phonenumber";
	private static final String KEY_AGE = "age";
	private static final String KEY_GENDER = "gender";
	private static final String KEY_CONTACT1 = "contact1";
	private static final String KEY_CONTACT2 = "contact2";
	private static final String KEY_CONTACT3 = "contact3";

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
				+ KEY_CREATED_AT + " TEXT"
				+ KEY_BLOODGROUP + " TEXT"
				+ KEY_PHONENUMBER + " TEXT"
				+ KEY_AGE + " TEXT"
				+ KEY_GENDER + " TEXT"
				+ KEY_CONTACT1 + " TEXT"
				+ KEY_CONTACT2 + " TEXT"
				+ KEY_CONTACT3 + " TEXT"
				+ ")";
		db.execSQL(CREATE_LOGIN_TABLE);

		Log.d(TAG, "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String name, String email, String uid, String created_at,String phonenumber,
						String bloodgroup,String age,String gender,String contact1,
						String contact2,String contact3) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name); // Name
		values.put(KEY_EMAIL, email); // Email
		values.put(KEY_UID, uid); // Email
		values.put(KEY_CREATED_AT, created_at); // Created At
		values.put(KEY_PHONENUMBER, phonenumber);
		values.put(KEY_BLOODGROUP, bloodgroup);
		values.put(KEY_AGE, age);
		values.put(KEY_GENDER, gender);
		values.put(KEY_CONTACT1, contact1);
		values.put(KEY_CONTACT2, contact2);
		values.put(KEY_CONTACT3, contact3);

		// Inserting Row
		long id = db.insert(TABLE_USER, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}

	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("name", cursor.getString(1));
			user.put("email", cursor.getString(2));
			user.put("uid", cursor.getString(3));
			user.put("created_at", cursor.getString(4));
			user.put("phonenumber", cursor.getString(5));
			user.put("bloodgroup", cursor.getString(6));
			user.put("age", cursor.getString(7));
			user.put("gender", cursor.getString(8));
			user.put("contact1", cursor.getString(9));
			user.put("contact2", cursor.getString(10));
			user.put("contact3", cursor.getString(11));

		}
		cursor.close();
		db.close();
		// return user
		Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

		return user;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void deleteUsers() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}

}
