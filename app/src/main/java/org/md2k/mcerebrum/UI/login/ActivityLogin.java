package org.md2k.mcerebrum.UI.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.md2k.mcerebrum.R;

import es.dmoral.toasty.Toasty;

public class ActivityLogin extends AppCompatActivity {
    BootstrapButton buttonLogin, buttonCancel;
    MaterialEditText editTextUserName, editTextPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonCancel = (BootstrapButton) findViewById(R.id.button_login_cancel);
        buttonLogin = (BootstrapButton) findViewById(R.id.button_login);
        editTextUserName= (MaterialEditText) findViewById(R.id.edittext_username);
        editTextPassword= (MaterialEditText) findViewById(R.id.edittext_password);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextUserName.getText().toString().equals("") ||
                        editTextPassword.getText().toString().equals("") ||
                        !editTextUserName.getText().toString().equals("abc") ||
                        !editTextPassword.getText().toString().equals("123")){
                    Toasty.error(ActivityLogin.this, "Error: Invalid Username and/or password", Toast.LENGTH_SHORT, true).show();
                }else{
                    Toasty.success(ActivityLogin.this, "Successfully Logged in", Toast.LENGTH_SHORT, true).show();
                    Intent intent = getIntent();
                    intent.putExtra("username", editTextUserName.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
