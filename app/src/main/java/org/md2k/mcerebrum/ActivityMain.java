package org.md2k.mcerebrum;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;

import org.md2k.mcerebrum.data.Data;
import org.md2k.mcerebrum.intro.ActivityIntro;
import org.md2k.mcerebrum.setup_step.ActivityConfig;
import org.md2k.mcerebrum.usage_type.ActivityUsageType;

import java.io.File;

import static com.blankj.utilcode.util.ZipUtils.unzipFile;

public class ActivityMain extends ActivityMenu {
    private static final int INTRO_ID=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      Data data=new Data();
        if(data.isFirstTimeRunning(this))
            showIntro();
    }
    void showIntro(){
        Intent intent=new Intent(this, ActivityIntro.class);
        startActivityForResult(intent, INTRO_ID);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("abc","abc");
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode==INTRO_ID) {
            Intent newIntent = new Intent(this, ActivityUsageType.class);
            startActivityForResult(newIntent, ID_JOIN_STUDY);
        }else if(requestCode==ID_JOIN_STUDY){
            switch(intent.getIntExtra("type",-1)){
                case ActivityUsageType.TYPE_LOGIN:
                case ActivityUsageType.TYPE_DOWNLOAD:
                case ActivityUsageType.TYPE_BARCODE:
                    boolean a=false;
                    String fileName=intent.getStringExtra("config_file");
//                    boolean b = unzipFile(fileName, folder.getAbsolutePath());
                    boolean b=unzipFile(fileName, this.getExternalFilesDir(null).toString()+"/temp");
                    Intent intentSettings=new Intent(this,ActivityConfig.class);
                    startActivity(intentSettings);
                    Log.d("abc","abc");

            }
            Data data=new Data();
            data.setFirstTimeRunning(this);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
    }
}

