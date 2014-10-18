package sk.ursus.modernexifeditor.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;

import sk.ursus.modernexifeditor.BuildConfig;

public class LOG {

	private static final String DEFAULT_TAG = "MainActivity";
	private static final boolean DEBUG = BuildConfig.DEBUG;

	public static String makeTag(Class<?> clazz) {
		return makeTag(clazz.getCanonicalName());
	}

	public static String makeTag(String tag) {
		return tag;
	}

	public static void d(String message) {
		d(DEFAULT_TAG, message);
	}

	public static void i(String message) {
		i(DEFAULT_TAG, message);
	}

	public static void e(String message) {
		e(DEFAULT_TAG, message);
	}

	public static void e(Exception e) {
		e(DEFAULT_TAG, e);
	}

	public static void d(String tag, String message) {
		if (DEBUG) {
			Log.d(tag, message);
		}
	}

	public static void i(String tag, String message) {
		if (DEBUG) {
			Log.i(tag, message);
		}
	}

	public static void e(String tag, String message) {
		if (DEBUG) {
			Log.e(tag, message);
		}
	}

	public static void e(String tag, Exception e) {
		if (DEBUG) {
			Log.e(tag, e.getMessage(), e);
		}
	}

	public static void dumpPojo(Object object) {
		dumpPojo(DEFAULT_TAG, object);
	}

	public static void dumpList(List<?> list, boolean reflect) {
		dumpList(DEFAULT_TAG, list, reflect);
	}

	public static void dumpCursor(Cursor cursor) {
		dumpCursor(DEFAULT_TAG, cursor);
	}

	public static void dumpRow(Cursor cursor) {
		dumpRow(DEFAULT_TAG, cursor);
	}

	public static void dumpPojo(String tag, Object object) {
		if (DEBUG) {
			StringBuilder sb = new StringBuilder();
			sb.append("### Dumping POJO ###\n");
			if (object == null) {
				sb.append("--- object is null ---\n");

			} else {
				pojoToString(object, sb);
			}
			sb.append("#########################");
			Log.i(tag, sb.toString());
		}
	}

	public static void dumpList(String tag, List<?> list, boolean reflect) {
		if (DEBUG) {
			StringBuilder sb = new StringBuilder();
			sb.append("### Dumping ArrayList ###\n");
			if (list == null) {
				sb.append("--- array list is null ---\n");

			} else {
				int i = 0;
				for (Object object : list) {
					sb.append("[" + i++ + "] ");
					if (reflect) {
						pojoToString(object, sb);
					} else {
						sb.append(object.toString());
					}
					sb.append("\n");
				}
			}
			sb.append("#########################");
			Log.i(tag, sb.toString());
		}
	}

	public static void dumpCursor(String tag, Cursor cursor) {
		if (DEBUG) {
			StringBuilder sb = new StringBuilder();
			sb.append("### Dumping cursor ###\n");
			if (cursor == null) {
				sb.append("--- cursor is null ---\n");

			} else {
				int startPos = cursor.getPosition();

				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					rowToString(cursor, sb);
				}
				cursor.moveToPosition(startPos);
			}
			sb.append("#########################");
			Log.i(tag, sb.toString());
		}
	}

	public static void dumpRow(String tag, Cursor cursor) {
		if (DEBUG) {
			StringBuilder sb = new StringBuilder();
			sb.append("### Dumping row\n");
			if (cursor == null) {
				sb.append("--- cursor is null ---\n");

			} else {
				rowToString(cursor, sb);
			}
			sb.append("###################");
			Log.i(DEFAULT_TAG, sb.toString());
		}
	}
	
	public static void isNull(Object object) {
		if (DEBUG) {
			if (object == null) {
				Log.e(DEFAULT_TAG, "Checked object is null");
			} else {
				String name = object.getClass().getSimpleName();
				Log.i(DEFAULT_TAG, name + " is not null");
			}
		}
	}

	private static void rowToString(Cursor cursor, StringBuilder sb) {
		String[] cols = cursor.getColumnNames();
		sb.append("" + cursor.getPosition() + " {\n");
		int length = cols.length;
		for (int i = 0; i < length; i++) {
			String value;
			try {
				value = cursor.getString(i);
			} catch (SQLiteException e) {
				value = "<unprintable>";
			}
			sb.append("   " + cols[i] + '=' + value + "\n");
		}
		sb.append("}\n");
	}

	private static void pojoToString(Object object, StringBuilder sb) {
		Class<? extends Object> clazz = object.getClass();
		sb.append(clazz.getCanonicalName() + " {\n");
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);

			String name = field.getName();
			if (name.equals("CREATOR")) {
				continue;
			}

			Object value;
			try {
				value = field.get(object);
			} catch (Exception e) {
				value = "<unprintable>";
			}
			sb.append("   " + name + '=' + value + "\n");
		}
		sb.append("}\n");
	}

}
