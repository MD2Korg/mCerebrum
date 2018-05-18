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

package org.md2k.mcerebrum.datakit.cerebralcortex;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.mcerebrum.datakit.configuration.Configuration;
import org.md2k.mcerebrum.datakit.configuration.ConfigurationManager;
import org.md2k.mcerebrum.core.access.appinfo.AppInfo;
import org.md2k.mcerebrum.core.access.serverinfo.ServerCP;

import java.io.IOException;

/**
 * Manages the publishing of data to <code>CerebralCortex</code>.
 */
public class CerebralCortexManager {

    /** This instance of this class. */
    private static CerebralCortexManager instance;

    /** Instance of <code>CerebralCortexWrapper</code>. */
    private static CerebralCortexWrapper task;

    /** Android context. */
    private Context context;

    /** Whether <code>CerebralCortex</code> is active or not. */
    private boolean active;

    /** Configuration object. */
    private Configuration configuration;

    /** Handler for messages. */
    private Handler handler;

    /**
     * Runnable that triggers uploading data to <code>CerebralCortex</code>.
     */
    private Runnable publishData = new Runnable() {
        /**
         * Recursively posts upload messages to the handler to push data to <code>CerebralCortex</code>
         * on the configured upload interval.
         */
        @Override
        public void run() {
            if(ServerCP.getServerAddress(context) == null) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("SERVER_ERROR"));
                stop();
                return;
            }
            if (task != null) {
                handler.removeCallbacks(publishData);
            }
            try {
                task = new CerebralCortexWrapper(context, configuration.upload.restricted_datasource);
                task.setPriority(Thread.MIN_PRIORITY);
                long time = AppInfo.serviceRunningTime(context.getApplicationContext(),
                                                            org.md2k.mcerebrum.datakit.Constants.SERVICE_NAME);
                if (time > 0) {
                    task.start();
                }
            } catch (IOException e) {}
            handler.postDelayed(publishData, configuration.upload.interval);
        }
    };

    /**
     * Constructor
     *
     * @param context Android context.
     */
    private CerebralCortexManager(Context context) {
        this.context = context;
        handler = new Handler();
        active = false;
        configuration=ConfigurationManager.read(context);


    }

    /**
     * Returns this instance of <code>CerebralCortexManager</code>.
     * @throws IOException
     */
    public static CerebralCortexManager getInstance(Context context) throws IOException {
        if (instance == null)
            instance = new CerebralCortexManager(context);
        return instance;
    }

    /**
     * Posts <code>publishData</code> messages to the handler.
     */
    void start() {
        if (configuration.upload.enabled) {
            active = true;
            handler.post(publishData);
        }
    }

    /**
     * Determines if <code>CerebralCortex</code> is active.
     * @return Whether <code>CerebralCortex</code> is active.
     */
    boolean isActive() {
        return active;
    }

    /**
     * Sets <code>CerebralCortex</code> to not active and removes any remaining
     * <code>publishData</code> callbacks from the handler.
     */
    void stop() {
        active = false;
        handler.removeCallbacks(publishData);
    }

    /**
     * <code>CerebralCortex</code> is available when <code>configuration</code> is not null.
     *
     * @return Whether <code>CerebralCortex</code> is available.
     */
    boolean isAvailable() {
        return (configuration != null);
    }
}
