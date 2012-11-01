package org.mannsverk.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mannsverk.common.enom.CalendarEnum;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static final String TAG = "FileUtil";

	public static boolean isMediaAvailable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state) ? true : false;
	}

	public static boolean isMediaWritable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ? false : true;
	}

	public static File getCachePath(Context context) {
		return context.getExternalCacheDir();
	}

	public static File existingCache(Context context, CalendarEnum type) {
		File cacheDirectory = FileUtil.getCachePath(context);
		String[] files = cacheDirectory.list();

		if(files.length >= 1) {
			for(String file : files) {
				if(file.endsWith(fileType(type))){
					Log.i(TAG, "Returning " + file);
					return new File(cacheDirectory, file);
				}
			}
		}
		return null;
	}

	private static String fileType(CalendarEnum type) {
		switch (type) {
		case EVENT: return ".par";
		case EVENTS: return ".cal";
		case SERVICE_UPDATE: return ".upd";
		}
		return null;
	}

	public static File cacheXML(InputStream inputStream, Context context, CalendarEnum type) {
		File cache = null;

		// only want to have one file in cache so deleting any existing before
		if (isMediaAvailable() && isMediaWritable() && deleteCache(context, type)) {
			// path to save the file
			File path = FileUtil.getCachePath(context);
			// file name is timestamp
			String fileName = Long.toString(System.currentTimeMillis()) + fileType(type);

			Log.i(TAG, "New cache is " + fileName);

			try {
				cache = new File(path, fileName);
				OutputStream out = new FileOutputStream(cache);
				byte buffer[] = new byte[1024];
				int length;
				while ((length = inputStream.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				out.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return existingCache(context, type);
			}
		}
		return cache;
	}

	private static boolean deleteCache(Context context, CalendarEnum type) {
		File cacheDirectory = FileUtil.getCachePath(context);
		String[] files = cacheDirectory.list();

		Log.i(TAG, "Found " + files.length + " files:");

		if (files != null && files.length > 0) {
			for (String file : files) {
				Log.i(TAG, file);
				if(file.endsWith(fileType(type))){
					File tmp = new File(cacheDirectory, file);
					Log.i(TAG, "File to delete: " + tmp.getAbsolutePath());
					boolean deleted = tmp.getAbsoluteFile().delete();
					if (deleted != true) {
						Log.i(TAG, "Could not delete cache");
						return false;
					}
				}
			}
		}
		return true;
	}

	public static void updateCache(Context context) {
		Log.i(TAG, "Update cache!");
		File cacheDirectory = FileUtil.getCachePath(context);
		String[] files = cacheDirectory.list();

		if (files != null && files.length > 0) {
			for (String file : files) {
				if(file.endsWith(".upd")){
					if(deleteCache(context, CalendarEnum.EVENTS)) {
						File old = new File(cacheDirectory, file);
						File cache = new File(cacheDirectory, file.substring(0, 13) + ".cal");
						if(old.renameTo(cache)) {
							Log.i(TAG, "Updated cache, new file is " + cache.getAbsolutePath());
						}
					}					
				}
			}
		}		
	}
}
