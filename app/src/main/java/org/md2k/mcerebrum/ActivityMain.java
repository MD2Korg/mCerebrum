package org.md2k.mcerebrum;

import android.content.Intent;
import android.os.Bundle;

import org.md2k.mcerebrum.menu.AbstractMenu;

public class ActivityMain extends AbstractActivityMenu {
    private static final int REQUEST_CODE = 100;
    boolean isFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isFirstTime=true;
    }
    @Override
    public void onResume(){
        super.onResume();
        if(!configManager.isConfigured(this)){
            Intent intent=new Intent(this, ActivityConfigureStudy.class);
            startActivityForResult(intent, REQUEST_CODE);
        }else{
            updateMenu(AbstractMenu.MENU_HOME);
            isFirstTime=false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE){
            if(resultCode==RESULT_CANCELED)
                finish();
            else{
                updateMenu(AbstractMenu.MENU_HOME);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

