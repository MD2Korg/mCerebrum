package org.md2k.mcerebrum.data;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
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

import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.core.datakitapi.source.platform.PlatformId;
import org.md2k.mcerebrum.core.datakitapi.source.platform.PlatformType;

import java.util.ArrayList;

public class PlatformList {
    ArrayList<PlatformInfo> platforms;

    public PlatformList() {
        platforms = new ArrayList<>();
        platforms.add(new PlatformInfo(PlatformType.PHONE, null));
        platforms.add(new PlatformInfo(PlatformType.MOTION_SENSE_HRV, PlatformId.LEFT_WRIST));
        platforms.add(new PlatformInfo(PlatformType.MOTION_SENSE_HRV, PlatformId.RIGHT_WRIST));
        platforms.add(new PlatformInfo(PlatformType.MOTION_SENSE_HRV_PLUS, PlatformId.LEFT_WRIST));
        platforms.add(new PlatformInfo(PlatformType.MOTION_SENSE_HRV_PLUS, PlatformId.RIGHT_WRIST));
    }
    public void setSample(String platformType, String platformId, String dataSourceType, String dataSourceId, long sample){
        for(int i = 0;i<platforms.size();i++){
            if(!platforms.get(i).platformType.equals(platformType)) continue;
            if(platforms.get(i).platformId==null || platforms.get(i).platformId.equals(platformId)){
                platforms.get(i).setSample(dataSourceType, dataSourceId, sample);
            }
        }
    }
    public ArrayList<PlatformInfo> getActiveList(){
        ArrayList<PlatformInfo> p = new ArrayList<>();
        for(int i = 0;i<platforms.size();i++){
            if(!platforms.get(i).active) continue;
            platforms.get(i).clear();
            p.add(platforms.get(i));
        }
        return p;
    }
}

class PlatformInfo {
    String title;
    String platformType;
    String platformId;
    boolean active;
    ArrayList<DataSourceInfo> dataSources;
    void setSample(String dataSourceType, String dataSourceId, long sample){
        active=true;
        for(int i = 0;i<dataSources.size();i++){
            if(!dataSources.get(i).dataSourceType.equals(dataSourceType)) continue;
            if(dataSources.get(i).dataSourceId!=null && dataSources.get(i).dataSourceId.equals(dataSourceId)) continue;
            if(dataSources.get(i).dataSourceId==null && dataSourceId!=null) continue;
            dataSources.get(i).setSampleNo(sample);
        }

    }

    PlatformInfo(String platformType, String platformId) {
        dataSources = new ArrayList<>();
        this.platformType=platformType;
        this.platformId = platformId;
        switch (platformType) {
            case PlatformType.PHONE:
                title="Phone";
                addPhoneSensor();
                break;
            case PlatformType.MOTION_SENSE_HRV:
                title="MotionSense HRV";
                if(platformId.equals(PlatformId.LEFT_WRIST)) title=title+" (Left Wrist)";
                else title=title+" (Right Wrist)";
                addMotionSenseHRV(platformId);
                break;
            case PlatformType.MOTION_SENSE_HRV_PLUS:
                title="MotionSense HRV+";
                if(platformId.equals(PlatformId.LEFT_WRIST)) title=title+" (Left Wrist)";
                else title=title+" (Right Wrist)";
                addMotionSenseHRVPlus(platformId);
                break;
        }
    }

    void addPhoneSensor() {
        dataSources.add(new DataSourceInfo("Accelerometer", DataSourceType.ACCELEROMETER, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", true));
        dataSources.add(new DataSourceInfo("Gyroscope", DataSourceType.GYROSCOPE, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", true));
        dataSources.add(new DataSourceInfo("Compass", DataSourceType.COMPASS, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", true));
        dataSources.add(new DataSourceInfo("Ambient Light", DataSourceType.AMBIENT_LIGHT, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("Pressure", DataSourceType.PRESSURE, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("Ambient Temperature", DataSourceType.AMBIENT_TEMPERATURE, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("Proximity", DataSourceType.PROXIMITY, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("Location", DataSourceType.LOCATION, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("Activity Type", DataSourceType.ACTIVITY_TYPE, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("App Usage", DataSourceType.CU_APPUSAGE, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("Call Information", DataSourceType.CU_CALL_TYPE, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("SMS Information", DataSourceType.CU_SMS_TYPE, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("Notification Post", DataSourceType.CU_NOTIF_POST_PACKAGE, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("Notification Removed", DataSourceType.CU_NOTIF_RM_PACKAGE, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("Touch Screen", DataSourceType.TOUCH_SCREEN, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("Step Count", DataSourceType.STEP_COUNT, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
        dataSources.add(new DataSourceInfo("Battery", DataSourceType.BATTERY, null, PlatformType.PHONE,null,null, "org.md2k.mcerebrum", false));
    }
    void addMotionSenseHRV(String platformId) {
        dataSources.add(new DataSourceInfo("Accelerometer", DataSourceType.ACCELEROMETER, null, PlatformType.MOTION_SENSE_HRV, platformId, null, "org.md2k.motionsense", true));
        dataSources.add(new DataSourceInfo("Gyroscope", DataSourceType.GYROSCOPE, null, PlatformType.MOTION_SENSE_HRV, platformId, null, "org.md2k.motionsense", true));
        dataSources.add(new DataSourceInfo("LED", DataSourceType.LED, null, PlatformType.MOTION_SENSE_HRV, platformId, null, "org.md2k.motionsense", true));
        dataSources.add(new DataSourceInfo("Battery", DataSourceType.BATTERY, null, PlatformType.MOTION_SENSE_HRV, platformId, null, "org.md2k.motionsense", false));
        dataSources.add(new DataSourceInfo("DataQuality(Accelerometer)", DataSourceType.DATA_QUALITY, DataSourceType.ACCELEROMETER, PlatformType.MOTION_SENSE_HRV, platformId, null, "org.md2k.motionsense", false));
        dataSources.add(new DataSourceInfo("DataQuality(LED)", DataSourceType.DATA_QUALITY, DataSourceType.LED, PlatformType.MOTION_SENSE_HRV, platformId, null, "org.md2k.motionsense", false));
    }
    void addMotionSenseHRVPlus(String platformId) {
        dataSources.add(new DataSourceInfo("Accelerometer", DataSourceType.ACCELEROMETER, null, PlatformType.MOTION_SENSE_HRV_PLUS, platformId, null, "org.md2k.motionsense", true));
        dataSources.add(new DataSourceInfo("Gyroscope", DataSourceType.GYROSCOPE, null, PlatformType.MOTION_SENSE_HRV_PLUS, platformId, null, "org.md2k.motionsense", true));
        dataSources.add(new DataSourceInfo("Quaternion", DataSourceType.QUATERNION, null, PlatformType.MOTION_SENSE_HRV_PLUS, platformId, null, "org.md2k.motionsense", true));
        dataSources.add(new DataSourceInfo("Magnetometer", DataSourceType.MAGNETOMETER, null, PlatformType.MOTION_SENSE_HRV_PLUS, platformId, null, "org.md2k.motionsense", true));
        dataSources.add(new DataSourceInfo("Magnetometer Sensitivity", DataSourceType.MAGNETOMETER_SENSITIVITY, null, PlatformType.MOTION_SENSE_HRV_PLUS, platformId, null, "org.md2k.motionsense", true));
        dataSources.add(new DataSourceInfo("LED", DataSourceType.LED, null, PlatformType.MOTION_SENSE_HRV_PLUS, platformId, null, "org.md2k.motionsense", true));
        dataSources.add(new DataSourceInfo("Battery", DataSourceType.BATTERY, null, PlatformType.MOTION_SENSE_HRV_PLUS, platformId, null, "org.md2k.motionsense", false));
        dataSources.add(new DataSourceInfo("DataQuality(Accelerometer)", DataSourceType.DATA_QUALITY, DataSourceType.ACCELEROMETER, PlatformType.MOTION_SENSE_HRV_PLUS, platformId, null, "org.md2k.motionsense", false));
    }

    public void clear() {
        ArrayList<DataSourceInfo> d = new ArrayList<>();
        for(int i=0;i<dataSources.size();i++)
            if(dataSources.get(i).active)
                d.add(dataSources.get(i));
        dataSources=d;
    }
}

class DataSourceInfo {
     String title;
    boolean plot;
    String dataSourceType;
    String dataSourceId;
    String platformType;
    String platformId;
    String applicationType;
    String applicationId;
    long sampleNo;
    boolean active;

    public DataSourceInfo(String title, String dataSourceType, String dataSourceId, String platformType, String platformId, String applicationType, String applicationId, boolean plot) {
        this.title = title;
        this.plot = plot;
        this.dataSourceType = dataSourceType;
        this.dataSourceId = dataSourceId;
        this.platformType = platformType;
        this.platformId = platformId;
        this.applicationType = applicationType;
        this.applicationId = applicationId;
        sampleNo = 0;
        active = false;
    }

    public void setSampleNo(long sampleNo) {
        this.sampleNo = sampleNo;
        this.active = true;
    }
}

