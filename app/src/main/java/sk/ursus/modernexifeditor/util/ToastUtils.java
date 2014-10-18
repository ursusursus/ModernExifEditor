package sk.ursus.modernexifeditor.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import sk.ursus.modernexifeditor.R;

public class ToastUtils {

	public static void show(Context context, String message) {
		showCustomToast(context, R.drawable.toast_regular_background, message, null);
	}

	public static void show(Context context, int stringResource) {
		showCustomToast(context, R.drawable.toast_regular_background, context.getString(stringResource), null);
	}

	public static void showSuccess(Context context, String message) {
		showCustomToast(context, R.drawable.toast_success_background, message, null);
	}

	public static void showSuccess(Context context, int stringResource) {
		showCustomToast(context, R.drawable.toast_success_background, context.getString(stringResource), null);
	}

	public static void showInfo(Context context, String message) {
		showCustomToast(context, R.drawable.toast_info_background, message, null);
	}

	public static void showInfo(Context context, int stringResource) {
		showCustomToast(context, R.drawable.toast_info_background, context.getString(stringResource), null);
	}

	public static void showError(Context context, String message) {
		showCustomToast(context, R.drawable.toast_error_background, message, null);
	}

	public static void showError(Context context, String message, String submessage) {
		showCustomToast(context, R.drawable.toast_error_background, message, submessage);
	}
	
	public static void showError(Context context, int stringResource) {
		showCustomToast(context, R.drawable.toast_error_background, context.getString(stringResource), null);
	}
	
	public static void showError(Context context, int stringResource, String submessage) {
		showCustomToast(context, R.drawable.toast_error_background, context.getString(stringResource), submessage);
	}

	public static void showError(Context context, int stringResource, int subStringResource) {
		showCustomToast(context, R.drawable.toast_error_background, context.getString(stringResource), context.getString(subStringResource));
	}

	private static void showCustomToast(Context context, int background, String message, String submessage) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View view = inflater.inflate(R.layout.custom_toast_layout, null);
		view.setBackgroundResource(background);

		TextView text = (TextView) view.findViewById(R.id.toastTextView);
		text.setText(message);

		if (submessage != null) {
			TextView subText = (TextView) view.findViewById(R.id.subToastTextView);
			subText.setVisibility(View.VISIBLE);
			subText.setText(submessage);
		}

		Toast toast = new Toast(context.getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view);
		toast.show();
	}

}
