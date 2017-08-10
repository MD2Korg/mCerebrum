package org.md2k.mcerebrum.Application;
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

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;

import org.md2k.mcerebrum.configuration.CApp;
import org.md2k.mcerebrum.configuration.CAppManager;

public class ApplicationManager {
    private Application[] applications;
    public void read(String filePath){
        CApp[] cApps=new CAppManager().read(filePath);
        applications=new Application[cApps.length];
        for(int i=0;i<cApps.length;i++){
            applications[i]=getApplication(cApps[i]);
        }
        //TODO: read from memory
    }
    public void update(){
        //TODO: read from github
    }
    private void write(){
        //TODO: write to memory
    }
    public Application[] get(){
        return applications;
    }
    public Application get(int i){
        return applications[i];
    }
    private Application getApplication(CApp cApp){
        Application application=new Application();
        application.id=cApp.getId();
        application.title=cApp.getTitle();
        application.summary=cApp.getSummary();
        application.description=cApp.getDescription();
        application.icon=cApp.getIcon();
        return application;
    }
}
