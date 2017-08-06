package org.md2k.mcerebrum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.md2k.mcerebrum.intro.ActivityIntro;
import org.md2k.mcerebrum.login.ActivityLogin;

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showIntro();
    }
    void showIntro(){
        Intent intent=new Intent(this, ActivityIntro.class);
        startActivityForResult(intent, 10);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("abc","abc");
        Intent intent=new Intent(this, ActivityLogin.class);
        startActivityForResult(intent, 10);
    }
}

