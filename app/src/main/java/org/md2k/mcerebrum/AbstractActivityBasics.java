package org.md2k.mcerebrum;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.md2k.mcerebrum.configuration.ConfigManager;
import org.md2k.mcerebrum.data.StudyInfo;
import org.md2k.mcerebrum.data.UserInfo;

public abstract class AbstractActivityBasics extends AppCompatActivity {
    static final String TAG=AbstractActivityBasics.class.getSimpleName();
    UserInfo userInfo;
    StudyInfo studyInfo;
    ConfigManager configManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo=new UserInfo();
        studyInfo=new StudyInfo();
        configManager=new ConfigManager();
    }
    abstract void updateMenu(int state);
}

