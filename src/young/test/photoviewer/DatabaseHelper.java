package young.test.photoviewer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper{
	
	private final static String DATABASE_NAME = "PhotoViewer.db";
	private final static int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		
	}
	
	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO what does this word mean?
		db.execSQL("CREATE TABLE IF NOT EXISTS pic" +  
		        "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, uri VARCHAR)");  
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE pic ADD COLUMN other STRING"); 
	}

}
