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

import android.os.Messenger;
import android.util.SparseArray;

import org.md2k.mcerebrum.datakit.logger.DatabaseLogger;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.status.Status;
import android.util.Log;

/**
 * Manages a sparse array of <code>Publisher</code> objects and provides methods for methods for handling them.
 */
public class Publishers {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = Publishers.class.getSimpleName();

    /** Array of <code>Publisher</code> objects. */
    private SparseArray<Publisher> publishers;

    /**
     * Constructor
     *
     * <p>
     *     The construction of this object is logged.
     * </p>
     */
    Publishers() {
        Log.d(TAG,"Publishers()...Constructor()...");
        publishers = new SparseArray<>();
    }

    /**
     * Sets the <code>databaseSubscriber</code> for the new <code>Publisher</code>.
     *
     * @param ds_id Data source identifier, used as a key for <code>publishers</code>.
     * @param databaseLogger Database logger to subscribe to.
     * @return The integer value associated with <code>Status.SUCCESS</code>, which is 0.
     */
    public int addPublisher(int ds_id, DatabaseLogger databaseLogger) {
        int status = addPublisher(ds_id);
        if (status == Status.SUCCESS)
            publishers.get(ds_id).setDatabaseSubscriber(new DatabaseSubscriber(databaseLogger));
        return Status.SUCCESS;
    }

    /**
     * Adds a new <code>Publisher</code> object to <code>publishers</code>.
     *
     * @param ds_id Data source identifier, used as a key for <code>publishers</code>.
     * @return The integer value associated with <code>Status.SUCCESS</code>, which is 0, or the
     *         integer value associated with <code>Status.DATASOURCE_INVALID</code>, which is 4.
     */
    public int addPublisher(int ds_id) {
        if (ds_id == -1)
            return Status.DATASOURCE_INVALID;
        if (publishers.indexOfKey(ds_id) < 0) {
            publishers.put(ds_id, new Publisher(ds_id));
        }
        return Status.SUCCESS;
    }

    /**
     * Adds a new <code>Publisher</code> to <code>publishers</code> if one for the given data source
     * identifier doesn't exist yet.
     *
     * @param ds_id Data source identifier, used as a key for <code>publishers</code>.
     * @return The integer value associated with <code>Status.SUCCESS</code>, which is 0, or the
     *         integer value associated with <code>Status.DATASOURCE_INVALID</code>, which is 4.
     */
    public int addSubscriber(int ds_id) {
        if (ds_id == -1)
            return Status.DATASOURCE_INVALID;
        if (publishers.indexOfKey(ds_id) < 0) {
            publishers.put(ds_id, new Publisher(ds_id));
        }
        return Status.SUCCESS;
    }

    /**
     * Calls <code>receivedData()</code> for the <code>Publisher</code> with the given data source
     * identifier.
     *
     * @param ds_id Data source identifier.
     * @param dataTypes Data types received.
     * @param isUpdate Whether the data is an update or not.
     * @return The status of the insertion of the received data.
     */
    public Status receivedData(int ds_id, DataType[] dataTypes, boolean isUpdate) {
        if (publishers.get(ds_id) != null)
            return publishers.get(ds_id).receivedData(dataTypes, isUpdate);
        else
            return new Status(Status.INTERNAL_ERROR);
    }

    /**
     * Calls <code>receivedDataHF()</code> for the <code>Publisher</code> with the given data source
     * identifier.
     *
     * @param ds_id Data source identifier.
     * @param dataTypes High frequency data types received.
     * @return The status of the insertion of the received data.
     */
    public Status receivedDataHF(int ds_id, DataTypeDoubleArray[] dataTypes) {
        if (publishers.get(ds_id) != null)
            return publishers.get(ds_id).receivedDataHF(dataTypes);
        else
            return new Status(Status.INTERNAL_ERROR);
    }

    /**
     * Sets the <code>DatabaseSubscriber</code> for the given <code>Publisher</code> to null.
     *
     * @param ds_id Data source identifier to remove.
     * @return The integer value associated with <code>Status.SUCCESS</code>, which is 0, or the
     *         integer value associated with <code>Status.DATASOURCE_NOT_EXIST</code>, which is 3.
     */
    public int remove(int ds_id) {
        if (!isExist(ds_id))
            return Status.DATASOURCE_NOT_EXIST;
        publishers.get(ds_id).setDatabaseSubscriber(null);
        return Status.SUCCESS;
    }

    /**
     * Determines if the <code>Publisher</code> associated with the given identifier is in the list.
     *
     * @param ds_id Data source identifier to find.
     * @return Whether the data source identifier exists in the list.
     */
    public boolean isExist(int ds_id) {
        return publishers.indexOfKey(ds_id) >= 0;
    }

    /**
     * Adds the given data source to the subscriber list and adds a <code>MessageSubscriber</code>
     * to the corresponding <code>Publisher</code>.
     *
     * @param ds_id identifier for the desired <code>publisher</code>.
     * @param packageName
     * @param reply Reference to message handler.
     * @return The integer value associated with <code>Status.SUCCESS</code>, which is 0, or the
     *         integer value associated with <code>Status.DATASOURCE_INVALID</code>, which is 4.
     */
    public int subscribe(int ds_id, String packageName, Messenger reply) {
        if(ds_id == -1 || packageName == null)
            return Status.DATASOURCE_INVALID;

        int status = addSubscriber(ds_id);

        if (status == Status.SUCCESS)
            status = publishers.get(ds_id).add(new MessageSubscriber(packageName, reply));

        return status;
    }

    /**
     * Removes the given <code>publisher</code>'s <code>MessageSubscriber</code>.
     *
     * @param ds_id identifier for the desired <code>publisher</code>.
     * @param packageName
     * @param reply Reference to message handler.
     * @return The integer value associated with <code>Status.SUCCESS</code>, which is 0, or the
     *         integer value associated with <code>Status.DATASOURCE_NOT_EXIST</code>, which is 3.
     */
    public int unsubscribe(int ds_id, String packageName, Messenger reply) {
        if (!isExist(ds_id))
            return Status.DATASOURCE_NOT_EXIST;

        return publishers.get(ds_id).remove(new MessageSubscriber(packageName, reply));
    }

    /**
     * Closes all <code>Publisher</code>s in the list.
     */
    public void close() {
        Log.d(TAG,"Publishers()...close()...");
        for (int i = 0; i < publishers.size(); i++) {
            int key = publishers.keyAt(i);
            publishers.get(key).close();
        }
        publishers.clear();
        publishers = null;
    }
}
