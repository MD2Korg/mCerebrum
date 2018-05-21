package org.md2k.mcerebrum.system.appinfo;
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

public class ApplicationManager {
/*
    private ArrayList<String> packageNames;
    protected Context context;
    public ApplicationManager(Context context){
        this.context = context;
        packageNames = AppCP.read(context);

    }
*/
/*
    public ArrayList<String> getByType(String type) {
        ArrayList<String> acs = new ArrayList<>();
        for(int i=0;i<packageNames.size();i++) {
            if (AppCP.getType(context, packageNames.get(i) != nu))
        }
        for (AppInfoController appInfoController : appInfoControllers) {
            if (appInfoController.getAppBasicInfo().isType(type))
                acs.add(appInfoController);
        }
        return acs;
    }


    public boolean isRequiredInstalled() {
        return getRequiredAppNotInstalled().size() == 0;
    }



*//*
*//*


*//*
    public void reset(String packageName) {
        AppInfoController appInfoController = getAppInfoController(packageName);
        if(appInfoController==null) return;
        boolean lastResult = appInfoController.getInstallInfoController().isInstalled();
        appInfoController.getInstallInfoController().reset();
        appInfoController.getAppAccess().reset();
//                appInfoControllers[i].getInstallInfoController().setInstalled();
        if (appInfoController.getInstallInfoController().isInstalled() != lastResult) {
            if (appInfoController.getInstallInfoController().isInstalled())
                appInfoController.getAppAccess().startService();
            else appInfoController.getAppAccess().stopService();
        }
    }

*//*
    Observable hasUpdate(String packageName) {
        AppInfoController appInfoController = getAppInfoController(packageName);
        if (appInfoController == null) return Observable.just(false);
        return appInfoController.getInstallInfoController().checkUpdate();
    }


    public Observable<Boolean> hasUpdate() {
        if (appInfoControllers == null || appInfoControllers.size() == 0)
            return Observable.just(false);
        ArrayList<Observable<Boolean>> observables=new ArrayList<>();
//        Observable[] observables = new Observable[getAppInfoControllers().length];
        for (int i = 0; i < appInfoControllers.size(); i++) {
            observables.add(appInfoControllers.get(i).getInstallInfoController().checkUpdate());
        }
        return Observable.merge(observables);
    }

    private AppInfoController getAppInfoController(String packageName) {
        for (AppInfoController appInfoController : appInfoControllers) {
            if (appInfoController.getPackageName().equals(packageName)) return appInfoController;
        }
        return null;
    }*/
}
