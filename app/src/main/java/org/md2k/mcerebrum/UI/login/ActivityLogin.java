package org.md2k.mcerebrum.UI.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.md2k.mcerebrum.R;

public class ActivityLogin extends AppCompatActivity {
    BootstrapButton buttonLogin, buttonCancel;
    MaterialEditText editTextUserName, editTextPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonCancel = (BootstrapButton) findViewById(R.id.button_login_cancel);
        buttonLogin = (BootstrapButton) findViewById(R.id.button_login);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
