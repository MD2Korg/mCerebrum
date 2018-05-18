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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.LocalBroadcastManager;
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
import android.util.Log;

/**
 * Preferences fragment for database settings
 */
public class PrefsFragmentSettingsDatabase extends PreferenceFragment {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = PrefsFragmentSettingsDatabase.class.getSimpleName();

    /** Configuration object. */
    Configuration configuration;

    /**
     * Creates the database settings screen.
     *
     * @param savedInstanceState Previous state of this activity, if it existed.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configuration = ConfigurationManager.read(getActivity());
        Log.d(TAG, "configuration=" + configuration);
        getPreferenceManager().getSharedPreferences().edit().clear().apply();
        getPreferenceManager().getSharedPreferences().edit().putString("key_storage", configuration.database.location).apply();
        addPreferencesFromResource(R.xml.pref_settings_database);
        setBackButton();
        setSaveButton();
        if (getActivity().getIntent().getBooleanExtra("delete", false))
            clearDatabase();
    }

    /**
     * Sets up preferences when the activity is resumed.
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
        ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setPadding(0, 0, 0, 0);

        return v;
    }

    /**
     * Creates a back button so the user can close this activity.
     */
    private void setBackButton() {
        final Button button = (Button) getActivity().findViewById(R.id.button_1);
        button.setText("Close");
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
        button.setText("Save");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
                configuration.database.location = sharedPreferences.getString("key_storage", configuration.database.location);
                ConfigurationManager.write(configuration);
                Toast.makeText(getActivity(), "Saved...", Toast.LENGTH_LONG).show();
                setupPreferences();
            }
        });
    }
    /**
     * Wrapper method for calling setup methods for database preferences.
     * <p>
     *     <ul>
     *         <li><code>setupStorage()</code></li>
     *         <li><code>setupDatabaseFile()</code></li>
     *         <li><code>setupDatabaseClear()</code></li>
     *         <li><code>setupSDCardSpace()</code></li>
     *         <li><code>setupDatabaseSize()</code></li>
     *     </ul>
     * </p>
     */
    void setupPreferences() {
        setupStorage();
        setupDatabaseFile();
        setupDatabaseClear();
        setupSDCardSpace();
        setupDatabaseSize();
    }
    /**
     * Sets up the storage location preferences.
     */
    void setupStorage() {
        ListPreference preference = (ListPreference) findPreference("key_storage");
        String storage = getPreferenceManager().getSharedPreferences().getString("key_storage",
            configuration.database.location);
        preference.setValue(storage);
        Log.d(TAG, "shared=" + storage + " config=" + configuration.database.location);
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
    private String findString(String[] values, String[] strings, String value) {
        for (int i = 0; i < values.length; i++)
            if (values[i].equals(value))
                return strings[i];
        return ("(not selected)");
    }

    /**
     * Sets up the SD card space preferences.
     */
    void setupSDCardSpace() {
        Preference preference = findPreference("key_sdcard_size");
        String location = getPreferenceManager().getSharedPreferences().getString("key_storage",
            configuration.database.location);
        preference.setSummary(FileManager.getLocationType(getActivity(), location) + " ["
            + FileManager.getSDCardSizeString(getActivity(), location) + "]");
    }

    /**
     * Sets up the size of the database file.
     */
    void setupDatabaseSize() {
        Preference preference = findPreference("key_file_size");
        String location = getPreferenceManager().getSharedPreferences().getString("key_storage",
            configuration.database.location);
        long fileSize = FileManager.getFileSize(getActivity(), location, Constants.DATABASE_FILENAME);
        preference.setSummary(FileManager.formatSize(fileSize));
    }

    /**
     * Sets up the database location preferences.
     */
    void setupDatabaseFile() {
        Preference preference = findPreference("key_directory");
        String location = getPreferenceManager().getSharedPreferences().getString("key_storage",
            configuration.database.location);
        String filename = FileManager.getDirectory(getActivity(), location) + Constants.DATABASE_FILENAME;
        preference.setSummary(filename);
    }

    /**
     * Sets up the "Clear database" perference option.
     */
    void setupDatabaseClear() {
        Preference preference = findPreference("key_delete");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /**
             * Clears the database if clicked.
             *
             * @param preference Preference clicked.
             * @return Always returns true.
             */
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearDatabase();
                return true;
            }
        });
    }

    /**
     * Sends a local broadcast with the given action string.
     *
     * @param str Action to broadcast.
     */
    void sendLocalBroadcast(String str) {
        Intent intent = new Intent("datakit");
        intent.putExtra("action", str);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    /**
     * Prompts the user for whether to delete the archive or not.
     */
    void clearDatabase() {
        Dialog.simple(getActivity(), "Clear Database", "Clear Database? \n\n"
            + "Data can't be recovered after deletion\n\n"
            + "Some apps may have problems after this operation. If it is, please restart those apps",
            "Yes", "Cancel", new DialogCallback() {
                /**
                 * Calls <code>DatabaseDeleteAsyncTask()</code> or <code>getActivity().finish()</code>
                 * accordingly.
                 *
                 * @param value Value of the selected dialog button.
                 */
            @Override
            public void onSelected(String value) {
                if (value.equals("Yes")) {
                    sendLocalBroadcast("stop");
                    new DatabaseDeleteAsyncTask().execute();
                } else {
                    if (getActivity().getIntent().getBooleanExtra("delete", false))
                        getActivity().finish();
                }

            }
        }).show();
    }

    /**
     * Nested class for asynchronously deleting the database data.
     */
    class DatabaseDeleteAsyncTask extends AsyncTask<String, String, String> {

        /** Dialog for showing the deletion progress. */
        private ProgressDialog dialog;

        /**
         * Constructor
         * <p>
         *     Creates a new <code>ProgressDialog</code>.
         * </p>
         */
        DatabaseDeleteAsyncTask() {
            dialog = new ProgressDialog(getActivity());
        }

        /**
         * Shows Progress Bar Dialog and then call doInBackground method
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Deleting database. Please wait...");
            dialog.show();
        }

        /**
         * Deletes the database directories in a background thread.
         *
         * @param strings Needed to properly override the method.
         * @return Null.
         */
        @Override
        protected String doInBackground(String... strings) {
            try {
                String location = ConfigurationManager.read(getActivity()).database.location;
                String filename = FileManager.getDirectory(getActivity(), location) + Constants.DATABASE_FILENAME;
                FileManager.deleteFile(filename);
            } catch (Exception ignored) {}
            return null;
        }

        /**
         * Sends a local "start" broadcast and dismisses the progress dialog and either finishes
         * the activity or calls <code>setPreferences()</code>.
         *
         * @param file_url Needed to properly override method.
         */
        @Override
        protected void onPostExecute(String file_url) {
            sendLocalBroadcast("start");
            setupPreferences();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getActivity(), "Database is Deleted", Toast.LENGTH_LONG).show();
            if (getActivity().getIntent().getBooleanExtra("delete", false))
                getActivity().finish();
        }
    }
}
