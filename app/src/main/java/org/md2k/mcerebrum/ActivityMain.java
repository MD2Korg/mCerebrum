package org.md2k.mcerebrum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.md2k.mcerebrum.intro.ActivityIntro;

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=new Intent(this, ActivityIntro.class);
        startActivity(intent);
    }
}
