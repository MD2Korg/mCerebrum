package org.md2k.mcerebrum.phonesensor;
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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.core.access.MCerebrum;
import org.md2k.mcerebrum.core.access.MCerebrumInfo;
import org.md2k.mcerebrum.phonesensor.plot.ActivityPlotChoice;

public class MyMCerebrumInit extends MCerebrumInfo {
    @Override
    public void update(final Context context){
        MCerebrum.setReportActivity(context, ActivityPlotChoice.class);
        MCerebrum.setBackgroundService(context, ServicePhoneSensor.class);
        MCerebrum.setConfigureActivity(context, ActivitySettings.class);
        MCerebrum.setPermissionActivity(context, ActivityPermission.class);
        MCerebrum.setConfigured(context, Configuration.isConfigured());
        MCerebrum.setConfigureExact(context, Configuration.isEqualDefault(MyApplication.getContext()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                myIntent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(myIntent);
            }
        }
        if(!MCerebrum.getPermission(context)) {
            Intent intent = new Intent(context, ActivityPermission.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
