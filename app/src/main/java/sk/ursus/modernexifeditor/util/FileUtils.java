package sk.ursus.modernexifeditor.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sk.ursus.modernexifeditor.R;

public class FileUtils {

	private static final String CAMERA_DIR = "/dcim/";
	public static final String MIME_TYPE_IMAGE = "image/jpeg";

	public static File createImageFile(Context context) throws IOException {
		String timestamp = new SimpleDateFormat(
				"yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());

		String filename = "IMG_" + timestamp + ".jpg";
		// File dir = context.getExternalFilesDir(null);

		File dir = getSystemGalleryDir(context.getString(R.string.app_name));
		if (dir != null && !dir.mkdirs() && !dir.exists()) {
			LOG.e("Failed to create directory");
			return null;
		} else {
			return new File(dir, filename);
		}
	}

	public static String getMimeType(Context context, Uri uri) {
		// Media stuff
		ContentResolver resolver = context.getContentResolver();
		String type = resolver.getType(uri);
		if (type != null) {
			return type;
		}

		// Files from filesystem
		String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
			if (type != null) {
				return type;
			}
		}

		// Atleast try
		return "*/*";

	}

	@SuppressLint("NewApi")
	/**
	 * Hacky hack
	 */
	public static String getPath(final Context context, final Uri uri) {
		if (Utils.hasKitkat() && DocumentsContract.isDocumentUri(context, uri)) {
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] parts = docId.split(":");
				final String type = parts[0];

				if (!"primary".equalsIgnoreCase(type)) {
					return null;
				} else {
					return Environment.getExternalStorageDirectory() + "/" + parts[1];
				}

			} else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] parts = docId.split(":");
				final String type = parts[0];

				Uri prefixUri = null;
				if ("image".equals(type)) {
					prefixUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					prefixUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					prefixUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						parts[1]
				};

				return getDataColumn(context, prefixUri, selection, selectionArgs);

			} else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);

			} else {
				return null;
			}
		} else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// MediaStore (and general)
			return getDataColumn(context, uri, null, null);
			
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			// File
			return uri.getPath();
		} else {
			return null;
		}
	}

	public static String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	private static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	private static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	private static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	private static File getSystemGalleryDir(String albumName) {
		return new File(Environment.getExternalStorageDirectory() + CAMERA_DIR + albumName);
	}

	public static boolean uriExists(Uri uri) {
		return new File(uri.getPath()).exists();
	}

	public static Uri encodeUri(Uri uri) {
		StringBuilder sb = new StringBuilder();
		sb.append("file://");
		for (String s : uri.getPathSegments()) {
			sb.append("/");
			sb.append(Uri.encode(s));
		}
		return Uri.parse(sb.toString());
	}

}
