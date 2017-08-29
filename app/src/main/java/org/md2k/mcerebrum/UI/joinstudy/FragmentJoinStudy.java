package org.md2k.mcerebrum.UI.joinstudy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.md2k.mcerebrum.R;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FragmentJoinStudy extends Fragment {
    FancyButton buttonLogin, buttonDownload, buttonSkip;
    MaterialEditText editTextUserName, editTextPassword, editTextServer, editTextDownload;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_join, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        buttonLogin = (FancyButton) view.findViewById(R.id.join_button_login);
        buttonDownload = (FancyButton) view.findViewById(R.id.join_button_download);
        buttonSkip = (FancyButton) view.findViewById(R.id.join_button_skip);
        editTextUserName= (MaterialEditText) view.findViewById(R.id.join_edittext_username);
        editTextPassword= (MaterialEditText) view.findViewById(R.id.join_edittext_password);
        editTextServer= (MaterialEditText) view.findViewById(R.id.join_edittext_server);
        editTextDownload= (MaterialEditText) view.findViewById(R.id.join_edittext_download);
        setupLogin();
        setupDownload();
        setupSkip();

    }
    void setupLogin(){
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    void setupDownload(){

    }
    void setupSkip(){

    }
}
