package org.md2k.mcerebrum.usage_type;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.login.ActivityLogin;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ActivityUsageType extends AppCompatActivity {
    public static final int TYPE_LOGIN=100;
    public static final int TYPE_GENERAL=101;
    public static final int TYPE_BARCODE=102;
    public static final int TYPE_DOWNLOAD=103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_usage_type);

        final Button button_login = (Button) findViewById(R.id.button_usage_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(ActivityUsageType.this, ActivityLogin.class);
                startActivityForResult(intent, TYPE_LOGIN);
            }
        });
        final Button button_general = (Button) findViewById(R.id.button_usage_general);
        button_general.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("type",TYPE_GENERAL);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        final Button button_download = (Button) findViewById(R.id.button_usage_download);
        button_download.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(ActivityUsageType.this,"Not implemented yet",Toast.LENGTH_LONG).show();
            }
        });
        final Button button_barcode = (Button) findViewById(R.id.button_usage_barcode);
        button_barcode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(ActivityUsageType.this,"Not implemented yet",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
       // delayedHide(100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == TYPE_LOGIN) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("type",   TYPE_LOGIN); setResult(Activity.RESULT_OK,returnIntent);
                returnIntent.putExtra("config_file",data.getStringExtra("config_file"));
                finish();
            }
        }
    }
    @Override
    public void onBackPressed(){

    }
}
