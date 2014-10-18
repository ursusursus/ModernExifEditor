package sk.ursus.modernexifeditor.util;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

import sk.ursus.modernexifeditor.R;


public class IntentUtils {

	public static final int CODE_TAKE_PICTURE = 1;
	public static final int CODE_RECORD_VOICE = 2;
	public static final int CODE_ATTACH_FILE = 3;
	public static final int CODE_RECOGNIZE_SPEECH = 4;

	public static Uri takePicture(Fragment f) {
		// Takze, ked chcem vytvorit normalnu velku foto
		// musim passnut EXTRA_OUTPUT parameter, s uri
		// na file kam to chcem savnut. V onActivityResult
		// potom bude intent null, takze si tu uri treba odlozit
		// uz tu pri vytvarani suboru
		//
		// Ak tam parameter EXTRA_OUTPUT nedam,
		// vytvorit sa iba thumbnail, ktory mi pride
		// v onActivityResult, v intent.getExtras().get("data");
		// a Uri bude null

		File file = null;
		Uri uri = null;
		try {
			file = FileUtils.createImageFile(f.getActivity());

		} catch (IOException e) {
			LOG.e(e);
		}

		if (file == null) {
			ToastUtils.show(f.getActivity(), R.string.imagefile_error);

		} else {
			uri = Uri.fromFile(file);

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			if (!Utils.isIntentAvailable(f.getActivity(), intent)) {
				ToastUtils.show(f.getActivity(), R.string.no_action);
			} else {
				f.startActivityForResult(intent, CODE_TAKE_PICTURE);
			}
		}

		return uri;
	}

	public static void addPictureToGallery(Fragment f, Uri uri) {
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intent.setData(uri);

		// Don't check for availability here
		// There's a bug probably because
		// it always returns false
		f.getActivity().sendBroadcast(intent);
	}

	public static void recordVoice(Fragment f) {
		Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);

		if (!Utils.isIntentAvailable(f.getActivity(), intent)) {
			ToastUtils.show(f.getActivity(), R.string.no_action);
			return;
		}

		f.startActivityForResult(intent, CODE_RECORD_VOICE);
	}

	@SuppressLint("NewApi")
	public static void attachFile(Fragment f) {
		Intent intent;
		if (Utils.hasKitkat()) {
			intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT);

		}

		// intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");

		if (!Utils.isIntentAvailable(f.getActivity(), intent)) {
			ToastUtils.show(f.getActivity(), R.string.no_action);
			return;
		}

		f.startActivityForResult(
				Intent.createChooser(intent, f.getString(R.string.choose_file)),
				CODE_ATTACH_FILE);
	}

	public static void open(Fragment f, Uri uri) {
		Context c = f.getActivity();
		if (!FileUtils.uriExists(uri)) {
			ToastUtils.show(c, R.string.file_doesnt_exist);
			return;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, FileUtils.getMimeType(c, uri));

		if (!Utils.isIntentAvailable(c, intent)) {
			ToastUtils.show(c, R.string.no_action);
			return;
		}

		f.startActivity(intent);
	}

	/* public static void openSound(Fragment f, Uri uri) {
		// Not using it for now in KitKat
		//
		// if (Utils.hasKitkat()) {
		// Intent intent = new Intent(Intent.ACTION_MAIN, uri);
		// intent.addCategory(Intent.CATEGORY_APP_MUSIC);
		// intent.setType("audio/*");
		// f.startActivity(Intent.createChooser(intent, "Prehra� cez..."));
		// }

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "audio/*");

		f.startActivity(Intent.createChooser(intent, "Prehra� cez..."));
	}

	public static void openImage(Fragment f, Uri uri) {
		// Not using it for now in KitKat
		//
		// if (Utils.hasKitkat()) {
		// Intent intent = new Intent(Intent.ACTION_MAIN, uri);
		// intent.addCategory(Intent.CATEGORY_GALLERY);
		// intent.setType("image/*");
		// f.startActivity(Intent.createChooser(intent, "Otvori� v..."));
		// }

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "image/*");

		f.startActivity(intent);
	}

	public static void openFile(Fragment f, Uri uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW); */
	// intent.setDataAndType(uri, "*/*");
	//
	// f.startActivity(intent);
	// }

	/* public static void shareNote(Fragment f, String title, String text, ArrayList<Uri> uris, String mimeType) {
		Intent intent;
		if (uris == null) {
			// Without attachments
			intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_SUBJECT, title);
			intent.putExtra(Intent.EXTRA_TEXT,
					text
							+ f.getString(R.string.share_signature)
							+ "\n\nhttps://play.google.com/store/apps/details?id="
							+ f.getActivity().getPackageName());
			intent.setType("text/plain");

		} else {
			if (uris.size() == 1) {
				// With one attachment
				intent = new Intent(Intent.ACTION_SEND);
				intent.putExtra(Intent.EXTRA_SUBJECT, title);
				intent.putExtra(Intent.EXTRA_TEXT, text);
				intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
				intent.setType(mimeType);

			} else {
				// With multiple attachments
				intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
				intent.putExtra(Intent.EXTRA_SUBJECT, title);
				intent.putExtra(Intent.EXTRA_TEXT, text);
				intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
				intent.setType("*///*");
	/*		}

		}

		if (!Utils.isIntentAvailable(f.getActivity(), intent)) {
			ToastUtils.show(f.getActivity(), R.string.no_action);
			return;
		}

		f.startActivity(Intent.createChooser(intent, f.getString(R.string.share_with)));

	} */

	public static void openBrowser() {
		//
	}

}
