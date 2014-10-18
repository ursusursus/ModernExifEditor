package sk.ursus.modernexifeditor;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getActionBar();
        //  actionBar.setDisplayUseLogoEnabled(false);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new ExifEditFragment())
                    .commit();
        }
    }

}
