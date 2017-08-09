package org.md2k.mcerebrum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;

import org.md2k.mcerebrum.data.Data;
import org.md2k.mcerebrum.intro.ActivityIntro;
import org.md2k.mcerebrum.usage_type.ActivityUsageType;

import java.io.File;

import static com.blankj.utilcode.util.ZipUtils.unzipFile;

public class ActivityMain extends ActivityMenu {
    private static final int PROFILE_SETTING = 1;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private static final int INTRO_ID=10;
    private static final int CHOICE_ID=11;

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
        if(requestCode==INTRO_ID) {
            Intent newIntent = new Intent(this, ActivityUsageType.class);
            startActivityForResult(newIntent, CHOICE_ID);
        }else if(requestCode==CHOICE_ID){
            switch(intent.getIntExtra("type",-1)){
                case ActivityUsageType.TYPE_LOGIN:
                case ActivityUsageType.TYPE_DOWNLOAD:
                case ActivityUsageType.TYPE_BARCODE:
                    String fileName=intent.getStringExtra("config_file");
                    unzipFile(fileName, this.getExternalFilesDir(null).toString()+"/temp");
                    Log.d("abc","abc");
            }
            Data data=new Data();
            data.setFirstTimeRunning(this);
        }
    }
}

