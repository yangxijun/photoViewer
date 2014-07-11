package young.test.photoviewer;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;



public class DBManager {
	private DatabaseHelper mHelper;
	private SQLiteDatabase mDb;
	
	public DBManager(Context context){
		mHelper = new DatabaseHelper(context);
		mDb = mHelper.getWritableDatabase();
	}
	
	
	// add image to Database
	public int addPicture(Image image){
		int tableId = -1;
		mDb.beginTransaction();
		try {
			ContentValues cv = new ContentValues();
			cv.put("name", image.getName());
			cv.put("uri", image.getUri());
			tableId = (int)mDb.insert("pic", null, cv);
			mDb.setTransactionSuccessful();
		} catch (SQLException e) {
			
		}
		finally{
			mDb.endTransaction();
		}
		return tableId;
	}
	
	// update image's name
	public boolean updateName(Image image){
		
		mDb.beginTransaction();
		try {
			
			ContentValues cv = new ContentValues();
			cv.put("name", image.getName());
			mDb.update("pic", cv, "_id = " + image.getId(), null);
			mDb.setTransactionSuccessful();
			
		} catch (SQLException e) {
			return false;
		}finally{
			mDb.endTransaction();
		}
		return true;
	}

	// delete image
	public boolean deletePicture(Image image) {

		mDb.beginTransaction();
		try {

			mDb.delete("pic", "_id = " + image.getId(), null);
			mDb.setTransactionSuccessful();

		} catch (SQLException e) {
			return false;
		} finally {
			mDb.endTransaction();
		}
		return true;
	}
	
	// find picture
	public List<Image> query() {

		ArrayList<Image> images = new ArrayList<Image>();
		Cursor cursor = queryTheCursor();
		while (cursor.moveToNext()) {
			Image image = new Image();
			image.setName(cursor.getString(cursor.getColumnIndex("name")));
			image.setUri(cursor.getString(cursor.getColumnIndex("uri")));
			//TODO use image cache
			image.setBitmap(BitmapFactory.decodeFile(image.getUri()));
			image.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			images.add(image);
			
		}
		cursor.close();
		return images;
	}
	
	public boolean queryIfExist(Image image) {
		Cursor cursor = mDb.query("pic", null, "name = ?",
				new String[] { image.getName() }, null, null, null);
		if (cursor.getCount() != 0) {
			return true;
		} else {
			return false;
		}
	}

	private Cursor queryTheCursor() {
		
		Cursor cursor = mDb.rawQuery("SELECT*FROM pic", null);
		return cursor;
	}
	
	public void closeDB(){
		mDb.close();
	}
}
