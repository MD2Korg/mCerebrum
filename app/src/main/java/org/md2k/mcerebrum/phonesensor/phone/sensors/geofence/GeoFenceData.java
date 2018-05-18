package org.md2k.mcerebrum.phonesensor.phone.sensors.geofence;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.core.access.appinfo.AppInfo;
import org.md2k.mcerebrum.phonesensor.ServicePhoneSensor;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class GeoFenceData {
    private Context context;
    private SharedPreferences sharedPref;
    private ArrayList<GeoFenceLocationInfo> geoFenceLocationInfos;

    public GeoFenceData(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(
                "geofence", Context.MODE_PRIVATE);
        read();
    }
    public String getGeoFenceString(){
        return sharedPref.getString("data", null);
    }

    private void read() {
        geoFenceLocationInfos = new ArrayList<>();
        String data = getGeoFenceString();
        if(data==null) return;
        String[] splits = data.split("#");
        if (splits.length == 0 || splits.length % 3 != 0) return;
        for (int i = 0; i < splits.length; i += 3) {
            String location = splits[i];
            double latitude = Double.parseDouble(splits[i + 1]);
            double longitude = Double.parseDouble(splits[i + 2]);
            geoFenceLocationInfos.add(new GeoFenceLocationInfo(location, latitude, longitude));
        }
    }
    public static void clearData(final Activity context){
        final SharedPreferences sharedPref = context.getSharedPreferences(
                "geofence", Context.MODE_PRIVATE);
        if(sharedPref.getString("data",null)==null){
            Toast.makeText(context, "Phone location info is already cleared", Toast.LENGTH_SHORT).show();
        }else {
            Dialog.simple(context, "Clear Location Information", "Do you want to clear location information?", "Yes", "Cancel", new DialogCallback() {
                @Override
                public void onSelected(String value) {
                    if("Yes".equals(value)){
                        SharedPreferences sharedPref = context.getSharedPreferences(
                                "geofence", Context.MODE_PRIVATE);
                        sharedPref.edit().remove("data").apply();
                        Toast.makeText(context, "Phone location info is cleared", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void add(GeoFenceLocationInfo g) {
        geoFenceLocationInfos.add(g);
        write();
    }
    public boolean isExist(String location){
        for (int i = 0; i < geoFenceLocationInfos.size(); i++)
            if (geoFenceLocationInfos.get(i).getLocation().equalsIgnoreCase(location)) {
                return true;
            }
            return false;
    }

    public void delete(String location) {
        for (int i = 0; i < geoFenceLocationInfos.size(); i++)
            if (geoFenceLocationInfos.get(i).getLocation().equalsIgnoreCase(location)) {
                geoFenceLocationInfos.remove(i);
                break;
            }
        write();
    }

    private void write() {
        boolean flag = AppInfo.isServiceRunning(context, ServicePhoneSensor.class.getName());
        if(flag) context.stopService(new Intent(context, ServicePhoneSensor.class));
        SharedPreferences.Editor editor = sharedPref.edit();
        String result=null;

        for(int i=0;i<geoFenceLocationInfos.size();i++){
            String l=geoFenceLocationInfos.get(i).getLocation();
            String lo= String.valueOf(geoFenceLocationInfos.get(i).getLongitude());
            String la= String.valueOf(geoFenceLocationInfos.get(i).getLatitude());
            if(result==null)
                result=l+"#"+la+"#"+lo;
            else result+="#"+l+"#"+la+"#"+lo;
        }
        if(result==null) result="";

        editor.putString("data", result);
        editor.apply();
        if(flag) context.startService(new Intent(context, ServicePhoneSensor.class));

    }

    public ArrayList<GeoFenceLocationInfo> getGeoFenceLocationInfos() {
        return geoFenceLocationInfos;
    }
}
