package org.md2k.mcerebrum;
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


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.reactnativenavigation.controllers.SplashActivity;

import org.md2k.mcerebrum.commons.permission.ActivityPermission;
import org.md2k.mcerebrum.commons.permission.PermissionInfo;
import org.md2k.mcerebrum.configuration.ConfigManager;

public class MainActivity extends SplashActivity {

    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        Intent intent = new Intent(this, ActivityPermission.class);
        startActivityForResult(intent, 1212);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1212) {
            if(data.getBooleanExtra(PermissionInfo.INTENT_RESULT, false)){
                ConfigManager.load(getApplicationContext(), ConfigManager.LOAD_TYPE.READ);
                Intent intent = new Intent(MainActivity.this, ServicePluginListener.class);
                startService(intent);
            }else{
                finish();
            }
        }
    }

}