package org.md2k.mcerebrum.UI.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.AppInfo;
import org.md2k.mcerebrum.app.ApplicationManager;
import org.md2k.mcerebrum.study.StudyInfo;
import org.md2k.mcerebrum.user.UserInfo;

import java.util.ArrayList;

import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_APP_ADD_REMOVE;
import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_APP_SETTINGS;

public class FragmentHome extends Fragment {
    ApplicationManager applicationManager;
    StudyInfo studyInfo;
    UserInfo UserInfo;

    AwesomeTextView awesomeTextViewSummary;
    AwesomeTextView awesomeTextViewInstall;
    AwesomeTextView awesomeTextViewSetup;
    BootstrapButton bootstrapButtonStart;
    AwesomeTextView awesomeTextViewInstallStatus;
    AwesomeTextView awesomeTextViewSetupStatus;
    View.OnClickListener onClickListernerInstall=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((ActivityMain)getActivity()).responseCallBack.onResponse(null, MENU_APP_ADD_REMOVE);
        }
    };

    View.OnClickListener onClickListernerSetup=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((ActivityMain)getActivity()).responseCallBack.onResponse(null, MENU_APP_SETTINGS);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_home, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        applicationManager = ((ActivityMain) getActivity()).applicationManager;
        studyInfo = ((ActivityMain) getActivity()).studyInfo;
        UserInfo = ((ActivityMain) getActivity()).userInfo;
        view.findViewById(R.id.app_install1).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.app_install2).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.app_install3).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.app_install4).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.app_install5).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.awesome_textview_install_status).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.awesome_textview_install).setOnClickListener(onClickListernerInstall);

        view.findViewById(R.id.app_setup1).setOnClickListener(onClickListernerSetup);
        view.findViewById(R.id.app_setup2).setOnClickListener(onClickListernerSetup);
        view.findViewById(R.id.app_setup3).setOnClickListener(onClickListernerSetup);
        view.findViewById(R.id.app_setup4).setOnClickListener(onClickListernerSetup);
        view.findViewById(R.id.app_setup5).setOnClickListener(onClickListernerSetup);
        view.findViewById(R.id.awesome_textview_setup_status).setOnClickListener(onClickListernerSetup);
        view.findViewById(R.id.app_setup6).setOnClickListener(onClickListernerSetup);
        view.findViewById(R.id.awesome_textview_setup).setOnClickListener(onClickListernerSetup);

        awesomeTextViewSummary = (AwesomeTextView) view.findViewById(R.id.awesome_textview_summary);
        awesomeTextViewInstall = (AwesomeTextView) view.findViewById(R.id.awesome_textview_install);
        awesomeTextViewInstallStatus = (AwesomeTextView) view.findViewById(R.id.awesome_textview_install_status);
        awesomeTextViewSetupStatus = (AwesomeTextView) view.findViewById(R.id.awesome_textview_setup_status);
        awesomeTextViewSetup = (AwesomeTextView) view.findViewById(R.id.awesome_textview_setup);
        bootstrapButtonStart = (BootstrapButton) view.findViewById(R.id.button_bootstrap_start);
        bootstrapButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain)getActivity()).startStudy();
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        updateSummary();
        updateInstall();
        updateSetup();

        if (!isCoreInstalled()) {
            bootstrapButtonStart.setEnabled(false);
            bootstrapButtonStart.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
            bootstrapButtonStart.setShowOutline(true);
        } else {
            bootstrapButtonStart.setEnabled(true);
            bootstrapButtonStart.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            bootstrapButtonStart.setShowOutline(false);
        }
    }

    private boolean isCoreInstalled() {
        if(!applicationManager.getDataKit().isInstalled()){
            return false;
        }
        if(applicationManager.getStudy()!=null && !applicationManager.getStudy().isInstalled()){
            return false;
        }
        return true;
    }

    void updateSummary() {
        awesomeTextViewSummary.setBootstrapText(getSummary());
    }

    void updateInstall() {
        String notInstalledApp = getRequiredAppNotInstalled();
        BootstrapText bt;
        if (notInstalledApp == null) {
            bt = new BootstrapText.Builder(getContext()).addText("Applications are installed")
                    .build();
            awesomeTextViewInstall.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewInstallStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewInstallStatus.setBootstrapText(getStatusText(true));
            awesomeTextViewInstall.setBootstrapText(bt);
            return;
        } else {
            bt = new BootstrapText.Builder(getContext()).addText("Please install: " + notInstalledApp)
                    .build();
            awesomeTextViewInstall.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewInstallStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewInstallStatus.setBootstrapText(getStatusText(false));
            awesomeTextViewInstall.setBootstrapText(bt);
            return;
        }
    }

    boolean updateSetup() {
        String notConfiguredApp = getRequiredAppNotSetup();
        BootstrapText bt;
        if (notConfiguredApp == null) {
            bt = new BootstrapText.Builder(getContext()).addText("Applications are configured.")
                    .build();
            awesomeTextViewSetup.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewSetupStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewSetupStatus.setBootstrapText(getStatusText(true));
            awesomeTextViewSetup.setBootstrapText(bt);
            return true;
        } else {
            bt = new BootstrapText.Builder(getContext()).addText("Please configure: " + notConfiguredApp)
                    .build();
            awesomeTextViewSetup.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewSetupStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewSetupStatus.setBootstrapText(getStatusText(false));
            awesomeTextViewSetup.setBootstrapText(bt);
            return false;
        }
    }

    BootstrapText getSummary() {
        return new BootstrapText.Builder(getContext()).addText("Study Title: " + studyInfo.getTitle() + "      User id:  " + UserInfo.getTitle())
                .build();
    }

    String getRequiredAppNotInstalled() {
        String notInstalledAppList="";
/*
        boolean flag=true;
        if(!applicationManager.getDataKit().isInstalled()){
            notInstalledAppList=applicationManager.getDataKit().getTitle();
            flag=false;
        }
        if(applicationManager.getStudy()!=null && !applicationManager.getStudy().isInstalled()){
            if(!flag) notInstalledAppList+=", "+applicationManager.getStudy().getTitle();
            else notInstalledAppList=applicationManager.getStudy().getTitle();
        }
        if(flag) return null;
        return notInstalledAppList;
*/


            ArrayList<AppInfo> appInfos = applicationManager.getRequiredAppNotInstalled();
        if(appInfos.size()==0) return null;
        for (int i = 0; i < appInfos.size(); i++) {
            if (applicationManager.getAppInfos()[i].isRequired()) {
                    if (i == 0)
                        notInstalledAppList = appInfos.get(i).getTitle();
                    else
                        notInstalledAppList += ", " + appInfos.get(i).getTitle();
            }
        }
        return notInstalledAppList;

    }

    String getRequiredAppNotSetup() {
        String notInstalledAppList="";
/*
        boolean flag=true;
        if(!applicationManager.getDataKit().isInstalled() || applicationManager.getDataKit().getInfo()==null || !applicationManager.getDataKit().getInfo().isConfigured()){
            notInstalledAppList=applicationManager.getDataKit().getTitle();
            flag=false;
        }
        if(applicationManager.getStudy()!=null && (!applicationManager.getStudy().isInstalled() || applicationManager.getStudy().getInfo()==null || !applicationManager.getStudy().getInfo().isConfigured())){
            if(!flag) notInstalledAppList+=", "+applicationManager.getStudy().getTitle();
            else notInstalledAppList=applicationManager.getStudy().getTitle();
        }
        if(flag) return null;
        return notInstalledAppList;
*/

        String notConfiguredAppList = null;
        int notConfiguredCount = 0;
        for (int i = 0; i < applicationManager.getAppInfos().length; i++) {
            if (applicationManager.getAppInfos()[i].isRequired()) {
                if (!applicationManager.getAppInfos()[i].isInstalled()) {
                    notConfiguredCount++;
                    if (notConfiguredCount == 1)
                        notConfiguredAppList = applicationManager.getAppInfos()[i].getTitle();
                    else
                        notConfiguredAppList += ", " + applicationManager.getAppInfos()[i].getTitle();
                } else if (applicationManager.getAppInfos()[i].isMCerebrumSupported()
                        && applicationManager.getAppInfos()[i].getInfo()!=null
                        && applicationManager.getAppInfos()[i].getInfo().isConfigurable()
                        && !applicationManager.getAppInfos()[i].getInfo().isEqualDefault()) {
                    notConfiguredCount++;
                    if (notConfiguredCount == 1)
                        notConfiguredAppList = applicationManager.getAppInfos()[i].getTitle();
                    else
                        notConfiguredAppList += ", " + applicationManager.getAppInfos()[i].getTitle();
                }
            }
        }
        return notConfiguredAppList;
    }

    BootstrapText getStatusText(boolean status) {
        if (status)
            return new BootstrapText.Builder(getContext()).addText("Status:   ").addFontAwesomeIcon("fa_check_circle")
                    .build();
        else
            return new BootstrapText.Builder(getContext()).addText("Status:   ").addFontAwesomeIcon("fa_times")
                    .build();
    }

}
