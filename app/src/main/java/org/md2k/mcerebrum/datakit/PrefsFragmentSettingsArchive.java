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

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.datakit.configuration.Configuration;
import org.md2k.mcerebrum.datakit.configuration.ConfigurationManager;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.commons.storage_old.FileManager;

/**
 * Preferences fragment for archive settings
 */
public class PrefsFragmentSettingsArchive extends PreferenceFragment {

    /** Configuration object. */
    Configuration configuration;

    /**
     * Creates the archive settings screen.
     *
     * @param savedInstanceState Previous state of this activity, if it existed.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configuration = ConfigurationManager.read(getActivity());
        getPreferenceManager().getSharedPreferences().edit().clear().apply();
        getPreferenceManager().getSharedPreferences().edit()
            .putBoolean("key_enabled",configuration.archive.enabled).apply();
        getPreferenceManager().getSharedPreferences().edit()
            .putString("key_storage",configuration.archive.location).apply();
        getPreferenceManager().getSharedPreferences().edit()
            .putString("key_interval",String.valueOf(configuration.archive.interval)).apply();
        addPreferencesFromResource(R.xml.pref_settings_archive);
        setBackButton();
        setSaveButton();
        if(getActivity().getIntent().getBooleanExtra("delete",false))
            clearArchive();
    }

    /**
     * Prompts the user for whether to delete the archive or not.
     */
    void clearArchive() {
        Dialog.simple(getActivity(), "Delete Archive Files?", "Delete Archive Files?"
            + "\n\nData can't be recovered after deletion", "Yes", "Cancel", new DialogCallback() {
            /**
             * Calls <code>ArchiveDeleteAsyncTask()</code> or <code>getActivity().finish()</code> accordingly.
             *
             * @param value Value of the selected dialog button.
             */
            @Override
            public void onSelected(String value) {
                if (value.equals("Yes")) {
                    new ArchiveDeleteAsyncTask().execute();
                }else{
                    if(getActivity().getIntent().getBooleanExtra("delete",false))
                        getActivity().finish();
                }
            }
        }).show();
    }

    /**
     * Sets up the "Clear archive" perference option.
     */
    void setupArchiveClear() {
        Preference preference = findPreference("key_delete");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            /**
             * Clears the archive if clicked.
             *
             * @param preference Preference clicked.
             * @return Always returns true.
             */
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearArchive();
                return true;
            }
        });
    }

    /**
     * When the activity is resumed, <code>setupPreferences()</code> is called.
     */
    @Override
    public void onResume() {
        setupPreferences();
        super.onResume();
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
        View v = super.onCreateView(inflater, container, savedInstanceState);
        assert v != null;
        ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setPadding(0, 0, 0, 0);

        return v;
    }

    /**
     * Creates a back button so the user can close this activity.
     */
    private void setBackButton() {
        final Button button = (Button) getActivity().findViewById(R.id.button_1);
        button.setText(R.string.button_close);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    /**
     * Creates a save button so the user can save the current configuration.
     */
    private void setSaveButton() {
        final Button button = (Button) getActivity().findViewById(R.id.button_2);
        button.setText(R.string.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
                configuration.archive.enabled = sharedPreferences.getBoolean("key_enabled", configuration.archive.enabled);
                configuration.archive.location = sharedPreferences.getString("key_storage", configuration.archive.location);
                configuration.archive.interval = Long.parseLong(sharedPreferences.getString("key_interval", String.valueOf(configuration.archive.interval)));
                if (configuration.archive.enabled && (configuration.archive.location == null || configuration.archive.interval == 0)) {
                    Toast.makeText(getActivity(), "Not Saved...not all values are set properly", Toast.LENGTH_LONG).show();
                    return;
                }
                ConfigurationManager.write(configuration);
                setupPreferences();
                Toast.makeText(getActivity(),"Saved...",Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Wrapper method for calling setup methods for archive preferences.
     * <p>
     *     <ul>
     *         <li><code>setupEnabled()</code></li>
     *         <li><code>setupStorage()</code></li>
     *         <li><code>setupDirectory()</code></li>
     *         <li><code>setupSize()</code></li>
     *         <li><code>setupSDCardSpace()</code></li>
     *         <li><code>setupArchiveClear()</code></li>
     *     </ul>
     * </p>
     */
    void setupPreferences() {
        setupEnabled();
        setupStorage();
        setupDirectory();
        setupSize();
        setupSDCardSpace();
        setupArchiveClear();
    }

    /**
     * Sets up a toggle preference for enabling/disabling the archive.
     */
    void setupEnabled(){
        SwitchPreference switchPreference = (SwitchPreference) findPreference("key_enabled");
        boolean enabled = getPreferenceManager().getSharedPreferences()
            .getBoolean("key_enabled", configuration.archive.enabled);
        switchPreference.setChecked(enabled);
    }

    /**
     * Sets up the SD card space preferences.
     */
    void setupSDCardSpace() {
        Preference preference = findPreference("key_sdcard_size");
        String location = getPreferenceManager().getSharedPreferences()
            .getString("key_storage", configuration.archive.location);
        preference.setSummary(FileManager.getLocationType(getActivity(), location)
            + " ["+FileManager.getSDCardSizeString(getActivity(), location)+"]");
    }

    /**
     * Sets up the size of the archive file.
     */
    void setupSize() {
        Preference preference = findPreference("key_file_size");
        String location = getPreferenceManager().getSharedPreferences()
            .getString("key_storage", configuration.archive.location);
        long fileSize = FileManager.getFileSize(getActivity(), location, Constants.RAW_DIRECTORY);
        preference.setSummary(FileManager.formatSize(fileSize));
    }

    /**
     * Sets up the storage location preferences.
     */
    void setupStorage() {
        ListPreference preference = (ListPreference) findPreference("key_storage");
        String storage = getPreferenceManager().getSharedPreferences()
            .getString("key_storage", configuration.archive.location);
        preference.setValue(storage);
        preference.setSummary(findString(getResources().getStringArray(R.array.sdcard_values),
            getResources().getStringArray(R.array.sdcard_text), storage));
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /**
             * Changes the storage location.
             *
             * @param preference Preference to change.
             * @param newValue New value of the preference.
             * @return Always returns false.
             */
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getPreferenceManager().getSharedPreferences().edit().putString("key_storage",
                    newValue.toString()).apply();
                setupPreferences();
                return false;
            }
        });
    }

    /**
     * Finds the given string inside of a string array.
     *
     * @param values String array created from <code>sdcard_values</code>.
     * @param strings String array created from <code>sdcard_text</code>.
     * @param value String to find.
     * @return The string that matches the value string.
     */
    private String findString(String[] values, String[] strings, String value){
        for(int i = 0; i < values.length; i++)
            if(values[i].equals(value))
                return strings[i];
        return ("(not selected)");
    }

    /**
     * Sets up the archive location preferences.
     */
    void setupDirectory() {
        Preference preference = findPreference("key_directory");
        String location = getPreferenceManager().getSharedPreferences().getString("key_storage",
            configuration.archive.location);
        String filename = FileManager.getDirectory(getActivity(), location) + Constants.ARCHIVE_DIRECTORY;
        preference.setSummary(filename);
    }

    /**
     * Nested class for asynchronously deleting the archive data.
     */
    class ArchiveDeleteAsyncTask extends AsyncTask<String, String, String> {

        /** Dialog for showing the deletion progress. */
        private ProgressDialog dialog;

        /**
         * Constructor
         * <p>
         *     Creates a new <code>ProgressDialog</code>.
         * </p>
         */
        ArchiveDeleteAsyncTask() {
            dialog = new ProgressDialog(getActivity());
        }

        /**
         * Shows Progress Bar Dialog and then call doInBackground method
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Deleting archive files. Please wait...");
            dialog.show();
        }

        /**
         * Deletes the archive directories in a background thread.
         *
         * @param strings Needed to properly override the method.
         * @return Null.
         */
        @Override
        protected String doInBackground(String... strings) {
            try {
                String location = ConfigurationManager.read(getActivity()).archive.location;
                String filename = FileManager.getDirectory(getActivity(), location) + Constants.ARCHIVE_DIRECTORY;
                FileManager.deleteFile(filename);
                String filename1 = FileManager.getDirectory(getActivity(), location) + Constants.RAW_DIRECTORY;
                FileManager.deleteFile(filename1);
            } catch (Exception ignored) {
            }
            return null;
        }

        /**
         * Dismisses the progress dialog and either finishes the activity or calls <code>setPreferences()</code>.
         *
         * @param file_url Needed to properly override method.
         */
        @Override
        protected void onPostExecute(String file_url) {
            setupPreferences();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getActivity(), "Archive files is Deleted", Toast.LENGTH_LONG).show();
            if(getActivity().getIntent().getBooleanExtra("delete",false))
                getActivity().finish();
        }
    }

}
