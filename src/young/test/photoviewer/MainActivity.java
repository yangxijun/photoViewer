package young.test.photoviewer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import young.test.photoviewer.R.string;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {


	
	private static final int RESULT_GET_IMAGE = 1;
	private Button mPickImage;
	private Button mAddToList;
	private Uri mUri;
	private Cursor mCursor;
	

	private Image mImage;
	private TextView mEditView; 
	private ImageView mPre;

	
	private ListView mListView;
	private View mRenameView;
	private List<Image> mData;
	private MyAdapter mAdapter;
	private ArrayList<String> mImageUri;
	private DBManager mDB;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mImage = new Image();
		mData = new ArrayList<Image>();
		mDB = new DBManager(this);
		mImageUri = new ArrayList<String>();

		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);

		new LoadData().execute();
		
		mEditView = (TextView) findViewById(R.id.editName);
		mPre = (ImageView) findViewById(R.id.preView);

		// pick a image
		mPickImage = (Button) findViewById(R.id.pickImage);
		mPickImage.setOnClickListener(this);

		// add to list
		mAddToList = (Button) findViewById(R.id.addToList);
		mAddToList.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

    //onClickListener
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.pickImage:
			getImage();
			break;
		case R.id.addToList:
			addImage();
			break;

		default:
			break;
		}
	}

	private void addImage() {

		// check the name and the uri of the image
		if (!(mEditView.getText().toString().equals(""))
				&& mImage.getUri() != null) {

			mImage.setName(mEditView.getText().toString());
			// check the rename
			if (!mDB.queryIfExist(mImage)) {
				
				int mTableId = mDB.addPicture(mImage);
				
				if (mTableId != -1) {
					
					Image newImage = new Image();
					newImage.setId(mImage.getId());
					newImage.setName(mImage.getName());
					newImage.setBitmap(mImage.getBitmap());
					newImage.setUri(mImage.getUri());
					mData.add(newImage);
					mAdapter.notifyDataSetChanged();
				}
			}
		}

	}

	private void getImage() {

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent,RESULT_GET_IMAGE);
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    super.onActivityResult(requestCode, resultCode, data);
	    if(requestCode == RESULT_GET_IMAGE){
	    	if(resultCode == RESULT_OK){
	    		
	    		//get image uri
	    		mUri = data.getData();
	    		ContentResolver mContentResolver = this.getContentResolver();
	    		
	    		//resolve uri
	    		mCursor = mContentResolver.query(mUri, null, null, null, null);
	    		mCursor.moveToFirst();
	    		
	    		try {
	    			mImage.clear();
					Bitmap bitmap = BitmapFactory.decodeStream(mContentResolver.openInputStream(mUri));
					mImage.setBitmap(bitmap);
					String name = mCursor.getString(5);
					mImage.setName(name);
					mImage.setUri(mCursor.getString(1));
					
					//show image and name
					mPre.setImageBitmap(bitmap);
					mEditView.setText(name);
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}
	    		finally{
	    			mCursor.close();
	    		}
	    	}
	    }
	}
	
	static class ViewHolder {  
        ImageView image;  
        TextView title;   
        Button delete;
        EditText oldName;
    }

	
	private class MyAdapter extends BaseAdapter{

		private Context mContext;
		private LayoutInflater layoutInflater;
		
		public MyAdapter(Context context){
			layoutInflater=LayoutInflater.from(context);
			this.mContext=context;
		}
		
		public int getCount() {
			
			return mData.size();
		}

		public Object getItem(int position) {
			// 
			return mData.get(position);
		}

		public long getItemId(int position) {
			// 
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			
			/* 
			if (convertView == null)
				convertView = layoutInflater.inflate(R.layout.listview, null);

			image = (ImageView) convertView.findViewById(R.id.image);
			image.setAdjustViewBounds(true);

			title = (TextView) convertView.findViewById(R.id.title);
			deleteImage = (Button) convertView.findViewById(R.id.delete);

			final Image currentImage = mData.get(position);

			image.setImageBitmap(currentImage.getBitmap());
			title.setText(currentImage.getName());

			// delete image
			deleteImage.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					//
					mData.remove(position);
					adapter.notifyDataSetChanged();
				}
			});

			// rename title
			title.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					//
					LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					renameView = inflater.inflate(R.layout.renameview, null);
					final EditText oldName = (EditText) renameView
							.findViewById(R.id.rename);
					oldName.setText(mImage.getName());

					new AlertDialog.Builder(MainActivity.this)
							.setTitle("重命名")
							.setView(renameView)
							.setPositiveButton("确认",
									new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog,int which) {
									
											String rename = oldName.getText()
													.toString();
											Image renameItem = new Image();
											renameItem.setName(rename);
											renameItem.setId(mImage.getId());
											mData.get(position).setName(rename);
											adapter.notifyDataSetChanged();

										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											//

										}
									}).show();

				}
			});
			
			 */
			
			// using ViewHolder to optimize
			ViewHolder viewHolder;
			if (null == convertView) {
				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.listview, null);

				viewHolder.image = (ImageView) convertView
						.findViewById(R.id.image);
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.title);
				viewHolder.delete = (Button) convertView
						.findViewById(R.id.delete);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			final Image currentImage = mData.get(position);
			viewHolder.image.setImageBitmap(currentImage.getBitmap());
			viewHolder.title.setText(currentImage.getName());
			
			// delete image
			viewHolder.delete.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					if (mDB.deletePicture(currentImage)) {
						mData.remove(position);
						mAdapter.notifyDataSetChanged();
					}
				}
			});
			
			// rename title
			viewHolder.title.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					//
					
					LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					mRenameView = inflater.inflate(R.layout.renameview, null);
					final EditText oldName = (EditText) mRenameView
							.findViewById(R.id.rename);
					oldName.setText(mImage.getName());


					new AlertDialog.Builder(MainActivity.this)
							.setTitle("重命名")
							.setView(mRenameView)
							.setPositiveButton("确认",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {

											String rename = oldName.getText()
													.toString();
											Image renameItem = new Image();
											renameItem.setName(rename);
											renameItem.setId(mImage.getId());
											if (mDB.updateName(renameItem)) {
												mData.get(position).setName(
														rename);
												mAdapter.notifyDataSetChanged();
											}

										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											//

										}
									}).show();

				}
			});
				
			// preview the images
			viewHolder.image.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, PictureView.class);
					for (Image img : mData) {
						mImageUri.add(img.getUri());
					}
					
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("imgUris", mImageUri);
					bundle.putInt("position", position);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});	

			
			
			/**/
			

			return convertView;
		}
		
		
		
	}
	
	
	class LoadData extends AsyncTask<Object, String, List<Image>>{

		@Override
		protected List<Image> doInBackground(Object... params) {
			
		    List<Image> temp = mDB.query();	
			return temp;
		}
		
		protected void onPostExecute(final List<Image> imageList){
			mData.clear();
			mData = imageList;
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (mData != null) {
			mData.clear();
		}
		if (mImageUri != null) {
			mImageUri.clear();
		}
		if (mListView != null) {
			mListView.setAdapter(null);
		}
		mDB.closeDB();
	}

	
	
	//save and load
	/*
	
	public void onPause(){
		super.onPause();
		//saveItems();
	}
	public void onResume(){
		super.onResume();
		//if(adapter.getCount()==0)
		//	loadItems();
	}
	/*
	private void loadItems() {
		// 
		BufferedReader reader = null;
		try {
			FileInputStream fis = openFileInput(FILE_NAME);
			reader = new BufferedReader(new InputStreamReader(fis));

			String name = null;
			String url = null;

			while (null != (name = reader.readLine())) {
				url = reader.readLine();
				
				adapter.add(new Image(name,url));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void saveItems(){
		// 
		PrintWriter writer = null;
		try {
			FileOutputStream fos=openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			writer=new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));
			
			for(int i=0;i<mData.getCount();i++)
				writer.println(adapter.getItem(i));
			
		} catch (Exception e) {
			// 
			e.printStackTrace();
		}finally{
			if(null!=writer)
				writer.close();
		}
		
	}*/
}
