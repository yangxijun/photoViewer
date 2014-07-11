package young.test.photoviewer;

import android.R.integer;
import android.R.string;
import android.graphics.Bitmap;

public class Image {
	
	private int mId;
	private String mName;
	private String mUri;
	private Bitmap mBitmap;
	
	public Image(){}
	public Image(String name,String uri){
		this.mName = name;
		this.mUri = uri;
	}
	
	public String getName(){
		return mName;
	}
	public void setName(String name){
		this.mName = name;
	}
	
	public String getUri(){
		return mUri;
	}
	public void setUri(String uri){
		this.mUri = uri;
	}
	
	public Bitmap getBitmap(){
		return mBitmap;
	}
	public void setBitmap(Bitmap bitmap){
		this.mBitmap = bitmap;
	}

	public int getId(){
		return mId;
	}
	public void setId(int id){
		this.mId = id;
	}
	
	public void clear(){
		mName = null;
		mUri = null;
		mBitmap = null;
	}

}
