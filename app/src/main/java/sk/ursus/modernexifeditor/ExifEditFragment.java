package sk.ursus.modernexifeditor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import sk.ursus.modernexifeditor.util.FileUtils;
import sk.ursus.modernexifeditor.util.IntentUtils;
import sk.ursus.modernexifeditor.util.LOG;
import sk.ursus.modernexifeditor.util.Meh;
import sk.ursus.modernexifeditor.util.ToastUtils;
import sk.ursus.modernexifeditor.util.Utils;

/**
 * Created by Ferko on 18.10.2014.
 */
public class ExifEditFragment extends Fragment {

    private Context mContext;
    private ExifInterface mExif;
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;
    private EditText mMakeEditText;
    private EditText mModelEditText;
    private EditText mDateTimeEditText;
    private EditText mApertureEditText;
    private EditText mFocalLengthEditText;
    private EditText mIsoEditText;
    private EditText mWhiteBalanceEditText;
    private EditText mOrientationEditText;
    private EditText mFlashEditText;
    private EditText mImageWidthEditText;
    private EditText mImageHeightEditText;
    private EditText mProcMethodEditText;
    private EditText mGpsTimestampEditText;
    private EditText mGpsAltEditText;
    private EditText mGpsAltRefEditText;
    private EditText mGpsLatEditText;
    private EditText mGpsLatRefEditText;
    private EditText mGpsLongEditText;
    private EditText mGpsLongRefEditText;
    private EditText mExposureEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exifedit2, container, false);
        mImageView = (ImageView) view.findViewById(R.id.imageView);
        mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
        mSubTitleTextView = (TextView) view.findViewById(R.id.subTitleTextView);

        mMakeEditText = (EditText) view.findViewById(R.id.manufacturerEditText);
        mModelEditText = (EditText) view.findViewById(R.id.modelEditText);
        mDateTimeEditText = (EditText) view.findViewById(R.id.datehEditText);

        mApertureEditText = (EditText) view.findViewById(R.id.apretureEditText);
        mExposureEditText = (EditText) view.findViewById(R.id.exposureEditText);
        mFocalLengthEditText = (EditText) view.findViewById(R.id.focalLengthEditText);
        mIsoEditText = (EditText) view.findViewById(R.id.isoEditText);
        mWhiteBalanceEditText = (EditText) view.findViewById(R.id.whiteBalanceEditText);
        mOrientationEditText = (EditText) view.findViewById(R.id.orientationEditText);
        mFlashEditText = (EditText) view.findViewById(R.id.flashEditText);

        mImageWidthEditText = (EditText) view.findViewById(R.id.imageWidthEditText);
        mImageHeightEditText = (EditText) view.findViewById(R.id.imageHeightEditText);
        mProcMethodEditText = (EditText) view.findViewById(R.id.procMethodEditText);

        mGpsTimestampEditText = (EditText) view.findViewById(R.id.gpsTimestampEditText);
        mGpsAltEditText = (EditText) view.findViewById(R.id.gpsAltEditText);
        mGpsAltRefEditText = (EditText) view.findViewById(R.id.gpsAltRefEditText);
        mGpsLatEditText = (EditText) view.findViewById(R.id.gpsLatEditText);
        mGpsLatRefEditText = (EditText) view.findViewById(R.id.gpsLatRefEditText);
        mGpsLongEditText = (EditText) view.findViewById(R.id.gpsLongEditText);
        mGpsLongRefEditText = (EditText) view.findViewById(R.id.gpsLongRefEditText);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_editexif, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                return true;
            case R.id.action_open:
                attachFile();
                return true;
            case R.id.action_settings:
                meh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        if(mExif == null) {
            return;
        }

        try {
            // naplnit atributy z edittextov
            mExif.saveAttributes();
            ToastUtils.showSuccess(mContext, "Saved successfully");

        } catch (IOException e) {
            LOG.e(e);
        }
    }

    private void meh() {
        mExif.setAttribute(ExifInterface.TAG_MODEL, "Kokotfonis");
        try {
            mExif.saveAttributes();
        } catch (IOException e) {
            LOG.e(e);
            }
    }

    private void attachFile() {
        IntentUtils.attachFile(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentUtils.CODE_ATTACH_FILE: {
                if (resultCode == Activity.RESULT_OK) {
                    handleFileAttached(data);
                }
                break;
            }

        }

    }

    private void handleFileAttached(Intent intent) {
        // Extract real path from URIs of various type
        String path = FileUtils.getPath(mContext, intent.getData());
        if (path == null) {
            LOG.e("Path is null");
            ToastUtils.show(mContext, R.string.unable_to_attach);
            return;
        }

        LOG.d("PATH=" + path);

        // Dummy file to get info
        File file = new File(path);
        if (!file.exists()) {
            LOG.e("File doesnt exist");
            ToastUtils.show(mContext, R.string.unable_to_attach);
            return;
        }

        Meh meh = new Meh();
        int bitmapWidth = 0;
        int bitmapHeight = 0;
        Bitmap bitmap = Utils.createThumbnail(getResources().getDisplayMetrics(), path, meh);
        if(bitmap != null) {
            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();
            mImageView.setImageBitmap(bitmap);
        }

        LOG.d("URI=" + file.getAbsolutePath());

        try {
            mExif = new ExifInterface(file.getAbsolutePath());

            printAttribute(ExifInterface.TAG_MAKE, mMakeEditText);
            printAttribute(ExifInterface.TAG_MODEL, mModelEditText);
            printAttribute(ExifInterface.TAG_DATETIME, mDateTimeEditText);
            printAttribute(ExifInterface.TAG_APERTURE, mApertureEditText);
            printAttribute(ExifInterface.TAG_FLASH, mFlashEditText);
            printAttribute(ExifInterface.TAG_EXPOSURE_TIME, mExposureEditText);
            printAttribute(ExifInterface.TAG_ISO, mIsoEditText);
            printAttribute(ExifInterface.TAG_WHITE_BALANCE, mWhiteBalanceEditText);
            printAttribute(ExifInterface.TAG_FOCAL_LENGTH, mFocalLengthEditText);
            printAttribute(ExifInterface.TAG_ORIENTATION, mOrientationEditText);
            printAttribute(ExifInterface.TAG_IMAGE_LENGTH, mImageHeightEditText);
            printAttribute(ExifInterface.TAG_IMAGE_WIDTH, mImageWidthEditText);
            printAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, mProcMethodEditText);
            printAttribute(ExifInterface.TAG_GPS_TIMESTAMP, mGpsTimestampEditText);
            printAttribute(ExifInterface.TAG_GPS_ALTITUDE, mGpsAltEditText);
            printAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, mGpsAltRefEditText);
            printAttribute(ExifInterface.TAG_GPS_LATITUDE, mGpsLatEditText);
            printAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, mGpsLatRefEditText);
            printAttribute(ExifInterface.TAG_GPS_LONGITUDE, mGpsLongEditText);
            printAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, mGpsLongRefEditText);

        } catch (IOException e) {
            LOG.e(e);
        }
        mTitleTextView.setText(file.getName());
        CharSequence fileSize = Utils.formatSize(file.length());
        if(bitmapWidth == 0 || bitmapHeight == 0) {
            mSubTitleTextView.setText(fileSize);
        } else {
            mSubTitleTextView.setText(fileSize + "  •  " + bitmapWidth + " x " + bitmapHeight);
        }
    }

    private void printAttribute(String tag, EditText editText) {
        if(mExif == null) {
            return;
        }

        String attribute = mExif.getAttribute(tag);
        if(attribute != null) {
            editText.setEnabled(true);
            editText.setText(attribute);
        } else {
            editText.setEnabled(false);
            editText.setText(getString(R.string.not_available));
        }
    }
}
