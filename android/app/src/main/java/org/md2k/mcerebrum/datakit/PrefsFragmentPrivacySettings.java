/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.md2k.mcerebrum.datakit;

import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.core.data_format.privacy.Duration;
import org.md2k.mcerebrum.core.data_format.privacy.PrivacyData;
import org.md2k.mcerebrum.core.data_format.privacy.PrivacyType;
import org.md2k.mcerebrum.datakit.configuration.ConfigurationManager;
import org.md2k.mcerebrum.datakit.configuration.PrivacyConfig;
import org.md2k.mcerebrum.datakit.privacy.PrivacyManager;
import org.md2k.mcerebrum.core.datakitapi.time.DateTime;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Preferences fragment for privacy settings
 */
public class PrefsFragmentPrivacySettings extends PreferenceFragment {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = PrefsFragmentPrivacySettings.class.getSimpleName();

    /** Privacy configuration object. */
    PrivacyConfig privacyConfig;

    /** Message handler. */
    Handler handler;

    /** PrivacyData object. */
    PrivacyData newPrivacyData;

    /** PrivacyManager object. */
    PrivacyManager privacyManager;

    /** Time remaining in the privacy duration. */
    long remainingTime;

    /**
     * Creates the privacy settings screen.
     *
     * @param savedInstanceState Previous state of this activity, if it existed.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate...");
        if(getActivity().getIntent().hasExtra("REMAINING_TIME")){
            remainingTime = getActivity().getIntent().getLongExtra("REMAINING_TIME",Long.MAX_VALUE);
        }else{
            remainingTime = Long.MAX_VALUE;
        }
        getPreferenceManager().getSharedPreferences().edit().clear().apply();
        privacyConfig=ConfigurationManager.read(getActivity()).privacy;
        if(privacyConfig==null){
            getActivity().finish();
            return;
        }
        newPrivacyData=new PrivacyData();
        handler = new Handler();
        addPreferencesFromResource(R.xml.pref_privacy);
        try {
            privacyManager = PrivacyManager.getInstance(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupButtonSaveCancel();
    }

    /**
     * Creates a <code>View</code>.
     *
     * @param inflater Android LayoutInflater
     * @param container Android ViewGroup
     * @param savedInstanceState Previous state of this activity, if it existed.
     * @return The <code>View</code> that was created.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout v = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        assert v != null;
        ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setPadding(0, 0, 0, 0);
        return v;
    }

    /**
     * Creates a start/stop toggle button and a cancel button.
     */
    void setupButtonSaveCancel() {
        final Button buttonStartStop = (Button) getActivity().findViewById(R.id.button_1);
        Button buttonCancel = (Button) getActivity().findViewById(R.id.button_2);

        buttonStartStop.setOnClickListener(new View.OnClickListener() {
            /**
             * Toggles the start/stop button.
             *
             * @param v Android view
             */
            @Override
            public void onClick(View v) {
/*
                if(privacyManager.isActive()){
                    PrivacyData privacyData = privacyManager.getPrivacyData();
                    privacyData.setStatus(false);
                    privacyManager.insertPrivacy(privacyData);
                    Toast.makeText(getActivity(), "Privacy Mode Off...", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    if(preparePrivacyData()) {
                        privacyManager.insertPrivacy(newPrivacyData);
                        Toast.makeText(getActivity(), "Privacy Mode On...", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }
*/
            }
        });
        buttonCancel.setText("Close");
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            /**
             * Calls <code>.finish()</code>.
             * @param v Android view.
             */
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    /**
     * Sets the starting timestamp and status of <code>newPrivacyData</code> as long as the duration
     * and privacy type are valid.
     *
     * @return Whether the preparation was successful.
     */
    boolean preparePrivacyData() {
        if(newPrivacyData.getDuration() == null) {
            Dialog.simple(getActivity(), "ERROR: Duration", "Duration is not set", "Ok", null, new DialogCallback() {
                @Override
                public void onSelected(String value) {}
            }).show();
            return false;
        }
        else if (newPrivacyData.getPrivacyTypes() == null || newPrivacyData.getPrivacyTypes().size() == 0) {
            Dialog.simple(getActivity(), "ERROR: Privacy Type", "Privacy Type is not selected", "Ok", null, new DialogCallback() {
                @Override
                public void onSelected(String value) {}
            }).show();
            return false;
        }
        else {
            newPrivacyData.setStartTimeStamp(DateTime.getDateTime());
            newPrivacyData.setStatus(true);
            return true;
        }
    }

    /**
     * Updates the user interface to reflect the current status of <code>privacyManager</code>.
     */
    void updateUI() {
        Preference preference = findPreference("status");
        preference.setEnabled(false);
        PreferenceCategory pc = (PreferenceCategory) findPreference("category_settings");

/*
        if (privacyManager.isActive()) {
            ((Button) getActivity().findViewById(R.id.button_1)).setText("Stop");
            pc.setEnabled(false);
            Spannable summary = new SpannableString("ON (" + DateTime
                                .convertTimestampToTimeStr(privacyManager.getRemainingTime()) + ")");
            summary.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.red_700)),
                                0, summary.length(), 0);
            preference.setSummary(summary);
        } else {
            ((Button) getActivity().findViewById(R.id.button_1)).setText("Start");
            pc.setEnabled(true);
            Spannable summary = new SpannableString("OFF");
            summary.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.teal_700)),
                                0, summary.length(), 0);
            preference.setSummary(summary);
        }
*/
    }

    /**
     * Runnable used to update the user interface every second.
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            {
                updateUI();
                handler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * Posts runnable messages on activity resume.
     */
    @Override
    public void onResume() {
        handler.post(runnable);
        super.onResume();
    }

    /**
     * Removes runnable messages currently in the handler when the activity is paused.
     */
    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    /**
     * Calls <code>setupPreferences()</code> when the activity is started.
     */
    @Override
    public void onStart(){
        if(privacyConfig==null){
            getActivity().finish();
            super.onStart();
            return;
        }
        setupPreferences();
        super.onStart();
    }

    /**
     * Wrapper method that calls <code>setupDuration()</code> and <code>setupPrivacyType()</code>.
     */
    void setupPreferences() {
        setupDuration();
        setupPrivacyType();
    }

    /**
     * Provides the user with a list of privacy types to choose from.
     */
    void setupPrivacyType() {
        final ArrayList<PrivacyType> privacyTypes = privacyConfig.privacy_type_options;
        final MultiSelectListPreference listPreference = (MultiSelectListPreference) findPreference("privacy_type");
        String[] entries = new String[privacyTypes.size()];
        String[] entryValues = new String[privacyTypes.size()];
        for (int i = 0; i < privacyTypes.size(); i++) {
            entries[i] = privacyTypes.get(i).getTitle();
            entryValues[i] = privacyTypes.get(i).getId();
        }
        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);
/*
        if (privacyManager.isActive()) {
            String list = "";
            for(int i = 0; i < privacyManager.getPrivacyData().getPrivacyTypes().size(); i++){
                if(!list.equals("")) list += ", ";
                list += privacyManager.getPrivacyData().getPrivacyTypes().get(i).getTitle();
            }
            listPreference.setSummary(list);
        } else {
            Spannable summary = new SpannableString("(Click Here)");
            summary.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.red_700)),
                0, summary.length(), 0);
            listPreference.setSummary(summary);
        }
*/
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /**
             * Sets the new privacy type.
             *
             * @param preference Preference to change.
             * @param newValue New value of the preference.
             * @return Whether the change was successful or not.
             */
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ArrayList<PrivacyType> privacyTypeSelected= new ArrayList<>();
                String list = "";
                for (int i = 0; i < privacyTypes.size(); i++){
                    if(((HashSet)newValue).contains(privacyTypes.get(i).getId())) {
                        privacyTypeSelected.add(privacyTypes.get(i));
                        if (!list.equals(""))
                            list += ", ";
                        list = list + privacyTypes.get(i).getTitle();
                    }
                }
                listPreference.setSummary(list);
                newPrivacyData.setPrivacyTypes(privacyTypeSelected);
                return false;
            }
        });
    }

    /**
     * Provides the user with a list of duration options to choose from.
     */
    void setupDuration() {
        final ArrayList<Duration> durations = privacyConfig.duration_options;
        final ListPreference listPreference = (ListPreference) findPreference("duration");
        ArrayList<String> entries = new ArrayList<>();
        ArrayList<String> entryValues = new ArrayList<>();
        for (int i = 0; i < durations.size(); i++) {
            if(durations.get(i).getValue() <= remainingTime) {
                entries.add(durations.get(i).getTitle());
                entryValues.add(durations.get(i).getId());
            }
        }
        listPreference.setEntries(entries.toArray(new String[entries.size()]));
        listPreference.setEntryValues(entryValues.toArray(new String[entryValues.size()]));
/*
        if (privacyManager.isActive()) {
            listPreference.setSummary(privacyManager.getPrivacyData().getDuration().getTitle());

        } else {
            Spannable summary = new SpannableString("(Click Here)");
            summary.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.red_700)),
                0, summary.length(), 0);
            listPreference.setSummary(summary);
        }
*/
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /**
             * Sets the new privacy duration.
             *
             * @param preference Preference to change.
             * @param newValue New value of the preference.
             * @return Whether the change was successful or not.
             */
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = listPreference.findIndexOfValue(newValue.toString());
                if (index >= 0) {
                    preference.setSummary(listPreference.getEntries()[index]);
                    listPreference.setValueIndex(index);
                    for (int i = 0; i < durations.size(); i++)
                        if (durations.get(i).getId().equals(newValue))
                            newPrivacyData.setDuration(durations.get(i));
                }
                return false;
            }
        });
    }
}
