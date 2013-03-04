package com.jcxavier.devbingo.logic;



import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	ImageView bmImage;

	public DownloadImageTask(final ImageView bmImage) {
		this.bmImage = bmImage;
	}

	@Override
	protected Bitmap doInBackground(final String... urls) {
		String urldisplay = urls[0];
		Bitmap mIcon11 = null;
		try {
			InputStream in = new java.net.URL(urldisplay).openStream();
			mIcon11 = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			android.util.Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		return mIcon11;
	}

	@Override
	protected void onPostExecute(final Bitmap result) {
		bmImage.setImageBitmap(result);
	}
}