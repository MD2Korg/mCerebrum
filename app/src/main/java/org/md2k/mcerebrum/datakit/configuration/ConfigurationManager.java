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

package org.md2k.mcerebrum.datakit.configuration;

import android.content.Context;

import com.google.gson.Gson;

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.datakit.Constants;
import org.md2k.mcerebrum.commons.storage_old.FileManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Manages the translation of configuration settings to and from JSON files and
 * <code>Configuration</code> objects.
 */
public class ConfigurationManager {
    private ConfigurationManager(Context context) {
        read(context);
    }

    public static Configuration read(Context context) {
        try {
            return FileManager.readJSON(Constants.CONFIG_DIRECTORY, Constants.CONFIG_FILENAME, Configuration.class);
        } catch (FileNotFoundException e) {
            return readDefault(context);
        }
    }

    private static Configuration readDefault(Context context) {
        BufferedReader br;
        br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.config)));
        Gson gson = new Gson();
        return gson.fromJson(br, Configuration.class);
    }
    public static void write(Configuration configuration){
        try {
            FileManager.writeJSON(Constants.CONFIG_DIRECTORY, Constants.CONFIG_FILENAME, configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
