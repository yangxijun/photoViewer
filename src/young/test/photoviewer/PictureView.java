package young.test.photoviewer;

import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class PictureView extends Activity{

	private Context mContext;
	private List<String> mUris;
	private List<String> mNames;
	private Bitmap mBitmap;

	private Gallery mGallery;
	private ImageView mImageView;
	private int mPosition;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.gallery);
		
		Bundle bundle = getIntent().getExtras();
		mUris = bundle.getStringArrayList("imgUris");
		mPosition = bundle.getInt("position");
		
		mGallery = (Gallery) findViewById(R.id.gallery);
		mGallery.setAdapter(new ImageAdapter(this));
	
		
	}
	
	public class ImageAdapter extends BaseAdapter{

		private Context mContext;
		
		public ImageAdapter(Context context){
			mContext = context;
		}
		
		
		public int getCount() {

			return mUris.size();
		}

		public Object getItem(int position) {

			return null;
		}

		public long getItemId(int position) {

			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			// TODO how to optimize
			mImageView = new ImageView(mContext);
			mBitmap = BitmapFactory.decodeFile(mUris.get(position));
			mImageView.setImageBitmap(mBitmap);
			

			return mImageView;
		}
		
	}
}
