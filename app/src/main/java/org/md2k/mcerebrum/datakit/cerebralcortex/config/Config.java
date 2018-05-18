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

import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;

import java.util.ArrayList;

/**
 * Defines a <code>Config</code> object to hold the <code>cerebralcortex</code> settings.
 */
public class Config {

    /** Minimum interval between uploads. */
    long upload_interval;

    /** URL to upload to. */
    String url;

    /** ArrayList of <code>DataSources</code> that <code>CerebralCortex</code> ignores. */
    ArrayList<DataSource> restricted_datasource = new ArrayList<>();

    /** How long, in milliseconds, data is kept in the <code>CerebralCortex</code> system. */
    long history_time;

    /**
     * Returns the configured upload interval.
     * @return The upload interval.
     */
    public long getUpload_interval() {
        return upload_interval;
    }

    /**
     * Sets the upload interval.
     * @param upload_interval The desired upload interval.
     */
    public void setUpload_interval(long upload_interval) {
        this.upload_interval = upload_interval;
    }

    /**
     * Returns the URL to upload to.
     * @return The url used for uploading.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the upload target URL.
     * @param url The URL to upload to.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns an arrayList of <code>DataSource</code> objects that <code>CerebralCortex</code> ignores.
     * @return The list of restricted <code>DataSource</code>s.
     */
    public ArrayList<DataSource> getRestricted_datasource() {
        return restricted_datasource;
    }

    /**
     * Sets the arrayList of <code>DataSource</code>s that do not get uploaded.
     * @param restricted_datasource Desired list of ignored <code>DataSource</code>s.
     */
    public void setRestricted_datasource(ArrayList<DataSource> restricted_datasource) {
        this.restricted_datasource = restricted_datasource;
    }

    /**
     * Returns whether the given <code>dataSourceType</code> exists in the
     * <code>restricted_datasource</code> list.
     *
     * @param dataSourceType <code>DataSource</code> to search for.
     * @return Whether the <code>DataSource</code> is present in the list.
     */
    public boolean isDataSourceExist(String dataSourceType) {
        if (restricted_datasource == null)
            return false;
        for (int i = 0; i < restricted_datasource.size(); i++)
            if (restricted_datasource.get(i).getType().equals(dataSourceType))
                return true;
        return false;
    }

    /**
     * Adds a <code>DataSource</code> to the <code>restricted_datasource</code> list.
     *
     * @param dataSourceType <code>DataSource</code> to add.
     */
    public void addDataSource(String dataSourceType) {
        DataSource dataSource = new DataSourceBuilder().setType(dataSourceType).build();
        if (restricted_datasource == null)
            restricted_datasource = new ArrayList<>();
        if (!isDataSourceExist(dataSourceType))
            restricted_datasource.add(dataSource);
    }

    /**
     * Removes a <code>DataSource</code> from the <code>restricted_datasource</code> list.
     *
     * @param dataSourceType <code>DataSource</code> to remove.
     */
    public void removeDataSource(String dataSourceType) {
        if (restricted_datasource == null)
            return;
        if (isDataSourceExist(dataSourceType))
            for (int i = 0; i < restricted_datasource.size(); i++) {
                if (restricted_datasource.get(i).getType().equals(dataSourceType)) {
                    restricted_datasource.remove(i);
                    break;
                }
            }
    }

    /**
     * Returns the value of <code>history_time</code>.
     *
     * @return The value of <code>history_time</code>.
     */
    public long getHistory_time() {
        return history_time;
    }

    /**
     * Sets the value of <code>history_time</code>.
     *
     * @param t The value <code>history_time</code> should be changed to.
     */
    public void setHistory_time(long t) {
        history_time = t;
    }
}
