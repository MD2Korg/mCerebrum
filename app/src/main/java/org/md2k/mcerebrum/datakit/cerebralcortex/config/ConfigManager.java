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

package org.md2k.mcerebrum.datakit.cerebralcortex.config;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.md2k.mcerebrum.commons.storage_old.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Provides methods for reading and manipulating configuration files.
 */
public class ConfigManager {

    /** Defines the directory of the configuration file. */
    public static final String CONFIG_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath()
                                                            + "/mCerebrum/org.md2k.cerebralcortex/";

    /** Defines the default configuration filename. <p>Default is <code>"default_config.json"</code>.</p> */
    public static final String DEFAULT_CONFIG_FILENAME = "default_config.json";

    /** Defines the non-default configuration filename. <p>Default is <code>"config.json"</code>.</p> */
    public static final String CONFIG_FILENAME = "config.json";

    /**
     * Reads the specified configuration file and returns a <code>Config</code> object.
     *
     * @throws FileNotFoundException
     */
    private static Config read(String filename) throws FileNotFoundException {
        Config config;
        if (!FileManager.isExist(CONFIG_DIRECTORY + filename))
            throw new FileNotFoundException();

        BufferedReader br = new BufferedReader(new FileReader(CONFIG_DIRECTORY + filename));
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Config>() {}.getType();
        config = gson.fromJson(br, collectionType);
        return config;
    }

    /**
     * Wrapper method for reading the non-default configuration file.
     * @throws FileNotFoundException
     */
    public static Config readConfig() throws FileNotFoundException {
        return read(CONFIG_FILENAME);
    }

    /**
     * Wrapper method for reading the default configuration file.
     * @throws FileNotFoundException
     */
    public static Config readDefaultConfig() throws FileNotFoundException {
        return read(DEFAULT_CONFIG_FILENAME);
    }

    /**
     * Writes configuration changes to a non-default configuration file.
     * @throws IOException
     */
    public static void write(Config config) throws IOException {
        File dir = new File(CONFIG_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Gson gson = new Gson();
        String json = gson.toJson(config);
        FileWriter writer = new FileWriter(CONFIG_DIRECTORY + CONFIG_FILENAME);
        writer.write(json);
        writer.close();
    }
}
