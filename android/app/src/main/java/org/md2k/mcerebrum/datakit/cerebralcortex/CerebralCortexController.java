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

import android.util.Log;

import java.io.IOException;

/**
 * Provides methods for determining if <code>CerebralCortex</code> is active and/or available.
 */
public class CerebralCortexController {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = CerebralCortexController.class.getSimpleName();

    /** This instance of this class. */
    private static CerebralCortexController instance = null;

    /**
     * Instance of the <code>cerebralCortexManager</code> class.
     *
     * <p>
     *     Used to connect to <code>CerebralCortex</code>
     * </p>
     */
    private static CerebralCortexManager cerebralCortexManager;

    /** Android context. */
    Context context;

    /**
     * Constructor
     *
     * <p>
     *     This constructor gets logged.
     * </p>
     *
     * @throws IOException
     */
    private CerebralCortexController(Context context) throws IOException {
        Log.d(TAG, "CerebralCortexController()...constructor()...");
        this.context = context;
        cerebralCortexManager = CerebralCortexManager.getInstance(context);
    }

    /**
     * Returns this instance of <code>CerebralCortexController</code>.
     *
     * @throws IOException
     */
    public static CerebralCortexController getInstance(Context context) throws IOException {
        if (instance == null)
            instance = new CerebralCortexController(context);
        return instance;
    }

    /**
     * Determines if <code>cerebralCortexManager</code> is actively running.
     *
     * @return Whether <code>cerebralCortexManager</code> is active.
     */
    public boolean isActive() {
        return cerebralCortexManager.isActive();
    }

    /**
     * Determines if <code>cerebralCortexManager</code> is available.
     * @return Whether <code>cerebralCortexManager</code> is available.
     */
    public boolean isAvailable() {
        return cerebralCortexManager.isAvailable();
    }
}
