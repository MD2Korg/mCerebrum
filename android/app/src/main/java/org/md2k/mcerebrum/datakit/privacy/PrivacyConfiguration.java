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

package org.md2k.mcerebrum.datakit.privacy;

import android.content.Context;

import org.md2k.mcerebrum.core.data_format.privacy.Duration;
import org.md2k.mcerebrum.core.data_format.privacy.PrivacyType;
import org.md2k.mcerebrum.datakit.configuration.ConfigurationManager;
import org.md2k.mcerebrum.datakit.configuration.PrivacyConfig;

import java.util.ArrayList;

/**
 * Manages privacy configuration for <code>PrivacyManager</code>.
 */
public class PrivacyConfiguration {

    /** Android context. */
    protected Context context;

    /** Contains privacy configuration options. */
    protected PrivacyConfig privacyConfig;

    /**
     * Constructor
     *
     * @param context Android context
     */
    public PrivacyConfiguration(Context context){
        this.context = context;
        privacyConfig = ConfigurationManager.read(context).privacy;
    }

    /**
     * Returns an arrayList of duration options.
     *
     * @return An arrayList of duration options.
     */
    public ArrayList<Duration> getDuration(){
        return privacyConfig.duration_options;
    }

    /**
     * Returns an arrayList of privacy type options.
     *
     * @return An arrayList of privacy type options.
     */
    public ArrayList<PrivacyType> getPrivacyType(){
        return privacyConfig.privacy_type_options;
    }

    /**
     * Returns whether <code>privacyConfig</code> is available or not.
     *
     * @return Whether <code>privacyConfig</code> is not null.
     */
    public boolean isAvailable(){
        return privacyConfig != null;
    }
}
