package org.md2k.mcerebrum;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.md2k.mcerebrum.configuration.ConfigManager;

public abstract class AbstractActivityBasics extends AppCompatActivity {
    static final String TAG=AbstractActivityBasics.class.getSimpleName();
    public static final int STATE_INIT=0;
    public static final int STATE_CONFIGURE_STUDY=1;
    int currentState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentState=STATE_INIT;
    }
    abstract void updateUI();
    void updateState(){
        int newState=currentState;
        ConfigManager configManager=new ConfigManager();
        if(!configManager.isConfigured(this))
            newState=STATE_CONFIGURE_STUDY;
        if(currentState!=newState) {
            currentState=newState;
            updateUI();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        updateState();
    }
}

