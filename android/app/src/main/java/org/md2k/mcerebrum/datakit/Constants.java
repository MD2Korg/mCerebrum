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

import android.os.Environment;

/**
 * This class defines various constants used throughout this application.
 */
public class Constants {

    /**
     * Directory for the configuration file.
     *
     * <p>
     *   The configuration file should be found in <code>"/mCerebrum/org.md2k.mcerebrum/"</code> by default.
     * </p>
     */
    public static final String CONFIG_DIRECTORY= Environment.getExternalStorageDirectory()
                                                            .getAbsolutePath() + "/mCerebrum/org.md2k.datakit/";

    /**
     * Name of the configuration file.
     *
     * <p>
     *   The configuration file is named <code>"config.json"</code> by default.
     * </p>
     */
    public static final String CONFIG_FILENAME = "config.json";

    /**
     * Filename of the database.
     *
     * <p>
     *   The filename for the database is <code>"database.db"</code> by default.
     * </p>
     */
    public static final String DATABASE_FILENAME="database.db";

    /**
     * Archive directory.
     *
     * <p>
     *   The default directory for archives is <code>"cerebralcortex/"</code>.
     * </p>
     */
    public static final String ARCHIVE_DIRECTORY="cerebralcortex/";

    /**
     * Directory for raw data.
     *
     * <p>
     *   The default raw data directory is <code>"raw/"</code>.
     * </p>
     */
    public static final String RAW_DIRECTORY = "raw/";

    /**
     * Name of this service.
     *
     * <p>
     *   The name of this service is <code>"org.md2k.mcerebrum.datakit.ServiceDataKit"</code> by default.
     * </p>
     */
    public static String SERVICE_NAME = "org.md2k.mcerebrum.datakit.ServiceDataKit";
}
