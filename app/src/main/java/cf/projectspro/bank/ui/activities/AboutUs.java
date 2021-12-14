package cf.projectspro.bank.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cf.projectspro.bank.R;

public class AboutUs extends AppCompatActivity {

    private TextView versionName, buildCode;
    private String mVersion, mBuild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        //Initialise UI Variables.
        versionName = findViewById(R.id.versionname);
        buildCode = findViewById(R.id.buildcode);

        //Get Version and Build of App.
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            mVersion = pInfo.versionName;
            mBuild = pInfo.versionCode + "";
            versionName.setText("v" + mVersion);
            buildCode.setText("Build " + mBuild);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
