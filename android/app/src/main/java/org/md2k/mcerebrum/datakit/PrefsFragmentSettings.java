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
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.datakit.configuration.Configuration;
import org.md2k.mcerebrum.datakit.configuration.ConfigurationManager;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.commons.storage_old.FileManager;

/**
 * Preferences fragment for general settings
 */
public class PrefsFragmentSettings extends PreferenceFragment {

    /**
     * Creates the general settings screen.
     *
     * @param savedInstanceState Previous state of this activity, if it existed.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        setBackButton();
        setPreferences();
        if(getActivity().getIntent().getBooleanExtra("delete",false))
            clearData();
    }

    /**
     * Prompts the user for whether to delete the database and archive or not.
     */
    void clearData() {
        Dialog.simple(getActivity(), "Delete Database & Archive Files?", "Delete Database & Archive Files?"
            + "\n\nData can't be recovered after deletion", "Yes", "Cancel", new DialogCallback() {
            /**
             * Calls <code>DeleteDataAsyncTask()</code> or <code>getActivity().finish()</code> accordingly.
             *
             * @param value Value of the selected dialog button.
             */
            @Override
            public void onSelected(String value) {
                if(value.equals("Yes")){
                    new DeleteDataAsyncTask().execute();
                }else {
                    if (getActivity().getIntent().getBooleanExtra("delete", false))
                        getActivity().finish();
                }
            }
        }).show();
    }

    /**
     * Nested class for asynchronously deleting the database and archive data.
     */
    class DeleteDataAsyncTask extends AsyncTask<String, String, String> {

        /** Dialog for showing the deletion progress. */
        private ProgressDialog dialog;

        /**
         * Constructor
         * <p>
         *     Creates a new <code>ProgressDialog</code>.
         * </p>
         */
        DeleteDataAsyncTask() {
            dialog = new ProgressDialog(getActivity());
        }

        /**
         * Shows Progress Bar Dialog and then call doInBackground method
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Deleting database & archive files. Please wait...");
            dialog.show();
        }

        /**
         * Deletes the archive and database directories in a background thread.
         *
         * @param strings Needed to properly override the method.
         * @return Null.
         */
        @Override
        protected String doInBackground(String... strings) {
            try {
                Configuration configuration = ConfigurationManager.read(getActivity());
                String location = configuration.archive.location;
                String directory = FileManager.getDirectory(getActivity(), location);
                FileManager.deleteDirectory(directory);
                location = configuration.database.location;
                if(!directory.equals(FileManager.getDirectory(getActivity(), location))) {
                    directory = FileManager.getDirectory(getActivity(), location);
                    FileManager.deleteDirectory(directory);
                }
            } catch (Exception ignored) {}
            return null;
        }

        /**
         * Dismisses the progress dialog and either finishes the activity or calls <code>setPreferences()</code>.
         *
         * @param file_url Needed to properly override method.
         */
        @Override
        protected void onPostExecute(String file_url) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(getActivity().getIntent().getBooleanExtra("delete",false))
                getActivity().finish();
            else
                setPreferences();
        }
    }

    /**
     * Wrapper method for calling set preference methods for database, archive, and upload settings.
     */
    public void setPreferences(){
        setPreferenceDatabase();
        setPreferenceArchive();
        setPreferenceUpload();
    }

    /**
     * Starts a <code>OnPreferenceClickListener</code> for the database preferences.
     */
    void setPreferenceDatabase(){
        Preference preference = findPreference("key_database");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /**
             * Starts the database settings activity if clicked.
             *
             * @param preference Database preference.
             * @return Always returns false.
             */
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(),ActivitySettingsDatabase.class);
                startActivity(intent);
                return false;
            }
        });
    }

    /**
     * Starts a <code>OnPreferenceClickListener</code> for the archive preferences.
     */
    void setPreferenceArchive(){
        Preference preference = findPreference("key_archive");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /**
             * Starts the archive settings activity if clicked.
             *
             * @param preference Archive preference.
             * @return Always returns false.
             */
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(),ActivitySettingsArchive.class);
                startActivity(intent);
                return false;
            }
        });
    }

    /**
     * Starts a <code>OnPreferenceClickListener</code> for the upload preferences.
     */
    void setPreferenceUpload(){
        Preference preference = findPreference("key_upload");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /**
             * Starts the upload settings activity if clicked.
             *
             * @param preference Upload preference.
             * @return Always returns false.
             */
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(),ActivitySettingsUpload.class);
                startActivity(intent);
                return false;
            }
        });
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
        View v = super.onCreateView(inflater, container,savedInstanceState);
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
            /**
             * Finishes the activity when clicked.
             *
             * @param v Android view.
             */
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }
}
