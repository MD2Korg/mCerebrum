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

import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.status.Status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Publishes messages on behalf of a particular <code>DataSource</code>.
 */
public class Publisher {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = Publisher.class.getSimpleName();

    /** Data source identifier. */
    int ds_id;

    /** List of message subscribers. */
    private List<MessageSubscriber> messageSubscribers;

    /** For interfacing with <code>DatabaseLogger</code> */
    private DatabaseSubscriber databaseSubscriber;

    /**
     * Constructor
     *
     * @param ds_id Data source identifier.
     */
    Publisher(int ds_id) {
        this.ds_id = ds_id;
        databaseSubscriber = null;
        messageSubscribers = new ArrayList<>();
    }

    /**
     * Clears <code>messageSubscribers</code> and sets <code>databaseSubscriber</code> to null.
     */
    public void close() {
        if (messageSubscribers != null) {
            messageSubscribers.clear();
            messageSubscribers = null;
        }
        databaseSubscriber = null;
    }

    /**
     * Sets <code>databaseSubscriber</code> to the given <code>DatabaseSubscriber</code>.
     *
     * @param databaseSubscriber Used for interfacing with <code>DatabaseLogger</code>.
     */
    public void setDatabaseSubscriber(DatabaseSubscriber databaseSubscriber) {
        this.databaseSubscriber = databaseSubscriber;
    }

    /**
     * Passes the received data to <code>notifyAllObservers</code> so it can be inserted into the
     * database.
     *
     * @param dataTypes Array of data types.
     * @param isUpdate Whether this data is an update or not.
     * @return The status after the received data has been inserted into the database.
     */
    public Status receivedData(DataType[] dataTypes, boolean isUpdate) {
        return notifyAllObservers(dataTypes, false, isUpdate);
    }

    /**
     * Passes the received data to <code>notifyAllObservers</code> so it can be inserted into the
     * database.
     *
     * @param dataTypes Array of high frequency data types.
     * @return The status after the received data has been inserted into the database.
     */
    public Status receivedDataHF(DataTypeDoubleArray[] dataTypes) {
        return notifyAllObservers(dataTypes, true, false);
    }

    /**
     * Checks if the given subscriber exists in the list.
     *
     * @param subscriber subscriber to find.
     * @return Whether the subscriber is in the list or not.
     */
    boolean isExists(MessageSubscriber subscriber) {
        return get(subscriber) != -1;
    }

    /**
     * Gets the index of the given subscriber from <code>messageSubscribers</code>.
     *
     * @param subscriber Subscriber to fetch.
     * @return The index of <code>messageSubscribers</code> where the subscriber is.
     */
    int get(MessageSubscriber subscriber) {
        for (int i = 0; i < messageSubscribers.size(); i++) {
            if (messageSubscribers.get(i).packageName.equals(subscriber.packageName))
                return i;
        }
        return -1;
    }

    /**
     * Adds a subscriber to the subscribers list.
     *
     * @param subscriber Subscriber to add.
     * @return The integer value associated with <code>Status.SUCCESS</code>, which is 0.
     */
    public int add(MessageSubscriber subscriber) {
        remove(subscriber);
        messageSubscribers.add(subscriber);
        return Status.SUCCESS;
    }

    /**
     * Removes a subscriber from the list.
     *
     * @param subscriber Subscriber to remove.
     * @return The integer value associated with <code>Status.SUCCESS</code>, which is 0.
     */
    public int remove(MessageSubscriber subscriber) {
        if (!isExists(subscriber))
            return Status.DATASOURCE_NOT_EXIST;
        messageSubscribers.remove(get(subscriber));
        return Status.SUCCESS;
    }

    /**
     * Notifies all observing message threads if the data was inserted successfully or not.
     *
     * @param dataTypes Array of data types to insert in the database.
     * @param highFrequency Whether the data is high frequency or not.
     * @param isUpdate Whether the data is an update or not.
     * @return The status of the insertion action.
     */
    public Status notifyAllObservers(DataType[] dataTypes, boolean highFrequency, boolean isUpdate) {
        Status status = new Status(Status.SUCCESS);
        if (databaseSubscriber != null) {
            if (highFrequency) {
                status = databaseSubscriber.insertHF(ds_id, (DataTypeDoubleArray[]) dataTypes);
            } else {
                status = databaseSubscriber.insert(ds_id, dataTypes, isUpdate);
            }
        }
        for (Iterator<MessageSubscriber> iterator = messageSubscribers.iterator(); iterator.hasNext(); ) {
            MessageSubscriber subscriber = iterator.next();
            if (!subscriber.update(ds_id, dataTypes))
                iterator.remove();
        }
        return status;
    }
}
