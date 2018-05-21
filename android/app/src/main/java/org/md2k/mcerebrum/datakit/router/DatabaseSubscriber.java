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

package org.md2k.mcerebrum.datakit.router;

import org.md2k.mcerebrum.datakit.logger.DatabaseLogger;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.status.Status;

/**
 * Manages calls to <code>DatabaseLogger</code>.
 */
public class DatabaseSubscriber {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = DatabaseSubscriber.class.getSimpleName();

    /** <code>DatabaseLogger</code> object used to interact with the database. */
    DatabaseLogger databaseLogger;

    /**
     * Constructor
     *
     * @param databaseLogger Used to interact with the database.
     */
    public DatabaseSubscriber(DatabaseLogger databaseLogger){
        this.databaseLogger = databaseLogger;
    }

    /**
     * Calls <code>databaseLogger</code> to insert the given data point(s).
     *
     * @param ds_id Data source identifier.
     * @param data Array of data types to insert.
     * @param isUpdate Whether this insertion is an update.
     * @return The status after insertion.
     */
    public Status insert(int ds_id, DataType[] data, boolean isUpdate) {
        return databaseLogger.insert(ds_id, data, isUpdate);
    }

    /**
     * Calls <code>databaseLogger</code> to insert high frequency data.
     *
     * @param ds_id Data source identifier.
     * @param data Array of data types to insert.
     * @return The status after insertion.
     */
    public Status insertHF(int ds_id, DataTypeDoubleArray[] data) {
        return databaseLogger.insertHF(ds_id, data);
    }

}