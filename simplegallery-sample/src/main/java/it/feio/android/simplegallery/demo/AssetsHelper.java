package it.feio.android.simplegallery.demo;


import android.content.Context;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;


public class AssetsHelper {

	public static byte[] getAsset(Context context, String name) throws IOException {
		InputStream is = context.getAssets().open("raw/" + name);
		byte[] imgDataBa = new byte[is.available()];
		DataInputStream dataIs = new DataInputStream(is);
		dataIs.readFully(imgDataBa);
		return imgDataBa;
	}
}
