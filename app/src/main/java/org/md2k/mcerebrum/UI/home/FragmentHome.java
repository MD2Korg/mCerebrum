package org.md2k.mcerebrum.UI.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.ApplicationManager;
import org.md2k.mcerebrum.data.DataManager;
import org.md2k.system.app.AppInfoController;
import org.md2k.system.constant.MCEREBRUM;

import java.util.ArrayList;

import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_APP_ADD_REMOVE;
import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_APP_SETTINGS;
import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_JOIN;

public class FragmentHome extends Fragment {
    ApplicationManager applicationManager;
    DataManager dataManager;
/*
    StudyInfoController studyInfoController;
    UserInfoController UserInfoController;
*/

    AwesomeTextView awesomeTextViewSummary;
    AwesomeTextView awesomeTextViewInstall;
    AwesomeTextView awesomeTextViewSetup;
    BootstrapButton bootstrapButtonStart;
    AwesomeTextView awesomeTextViewInstallStatus;
    AwesomeTextView awesomeTextViewSetupStatus;
    TextView textViewClickInstall;
    TextView textViewClickSetup;
    TextView textViewClickJoin;
    View.OnClickListener onClickListernerInstall = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MENU_APP_ADD_REMOVE);
        }
    };
    View.OnClickListener onClickListernerJoin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(dataManager.getDataCPManager().getConfigCP().getType().equalsIgnoreCase(MCEREBRUM.CONFIG.TYPE_FREEBIE))
                ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MENU_JOIN);
        }
    };

    View.OnClickListener onClickListernerSetup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MENU_APP_SETTINGS);
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
        dataManager = ((ActivityMain) getActivity()).dataManager;

//        studyInfoController = ((ActivityMain) getActivity()).studyInfoController;
//        UserInfoController = ((ActivityMain) getActivity()).userInfoController;
        view.findViewById(R.id.join1).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join2).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join3).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join4).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join5).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join6).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join7).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.awesome_textview_summary).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.textview_join).setOnClickListener(onClickListernerJoin);
/*
        if(dataManager.getDataCPManager().getStudyCP().getType().equalsIgnoreCase(STUDY.FREEBIE)){
            ((LinearLayout) view.findViewById(R.id.join1)).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_red_900));
        }
        else if(dataManager.getDataCPManager().getStudyCP().getType().equalsIgnoreCase(STUDY.SERVER)){
            ((LinearLayout) view.findViewById(R.id.join1)).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_cyan_900));
        }
*/


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
                ((ActivityMain) getActivity()).startStudy();
            }
        });

        textViewClickInstall = (TextView) view.findViewById(R.id.textview_install);
        textViewClickSetup = (TextView) view.findViewById(R.id.textview_setup);
        textViewClickJoin = (TextView) view.findViewById(R.id.textview_join);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSummary();
        updateInstall();
        updateSetup();
        updateButton();
    }
    void updateButton(){
        if (!applicationManager.isCoreInstalled()) {
            bootstrapButtonStart.setEnabled(false);
            bootstrapButtonStart.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            bootstrapButtonStart.setBootstrapText(new BootstrapText.Builder(getActivity()).addFontAwesomeIcon("fa-ban").addText("  Start Study").build());
            bootstrapButtonStart.setShowOutline(true);
        } else {
            bootstrapButtonStart.setEnabled(true);
            bootstrapButtonStart.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            bootstrapButtonStart.setBootstrapText(new BootstrapText.Builder(getActivity()).addFontAwesomeIcon("fa-play-circle-o").addText("  Start Study").build());
            bootstrapButtonStart.setShowOutline(false);
        }

    }

    void updateSummary() {
        awesomeTextViewSummary.setBootstrapText(getSummary());
        if(dataManager.getDataCPManager().getConfigCP().getType().equalsIgnoreCase(MCEREBRUM.CONFIG.TYPE_FREEBIE)) {
            textViewClickJoin.setVisibility(View.VISIBLE);
            awesomeTextViewSummary.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
        }
        else {
            awesomeTextViewSummary.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            textViewClickJoin.setVisibility(View.INVISIBLE);
        }

    }

    void updateInstall() {
        ArrayList<AppInfoController> appInfos = applicationManager.getRequiredAppNotInstalled();
//        String notInstalledApp = getRequiredAppNotInstalled();
        BootstrapText bt;
        if (appInfos.size()==0) {
            bt = new BootstrapText.Builder(getContext()).addText("Installation Successful")
                    .build();
            awesomeTextViewInstall.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewInstallStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewInstallStatus.setBootstrapText(getStatusText(true));
            awesomeTextViewInstall.setBootstrapText(bt);
        } else {
            bt = new BootstrapText.Builder(getContext()).addText("Not installed: " + appInfos.size()+" apps")
                    .build();
            awesomeTextViewInstall.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewInstallStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewInstallStatus.setBootstrapText(getStatusText(false));
            awesomeTextViewInstall.setBootstrapText(bt);
        }
    }

    boolean updateSetup() {
        ArrayList<AppInfoController> appInfos = applicationManager.getRequiredAppNotConfigured();
//        String notConfiguredApp = getRequiredAppNotSetup();
        BootstrapText bt;
        if (appInfos.size() == 0) {
            bt = new BootstrapText.Builder(getContext()).addText("Application Setup Successful")
                    .build();
            awesomeTextViewSetup.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewSetupStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewSetupStatus.setBootstrapText(getStatusText(true));
            awesomeTextViewSetup.setBootstrapText(bt);
            return true;
        } else {
            bt = new BootstrapText.Builder(getContext()).addText("Not Configured: " + appInfos.size()+" apps")
                    .build();
            awesomeTextViewSetup.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewSetupStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewSetupStatus.setBootstrapText(getStatusText(false));
            awesomeTextViewSetup.setBootstrapText(bt);
            return false;
        }
    }

    BootstrapText getSummary() {
        if(dataManager.getDataCPManager().getStudyCP().getType().equalsIgnoreCase(MCEREBRUM.CONFIG.TYPE_FREEBIE))
        return new BootstrapText.Builder(getContext()).addText("General Use")
                .build();
        else
        return new BootstrapText.Builder(getContext()).addText(dataManager.getDataCPManager().getStudyCP().getTitle())
                .build();
    }

    String getRequiredAppNotInstalled() {
        String notInstalledAppList = "";
        ArrayList<AppInfoController> appInfos = applicationManager.getRequiredAppNotInstalled();
        if (appInfos.size() == 0) return null;
        for (int i = 0; i < appInfos.size(); i++) {
            if (i == 0)
                notInstalledAppList = appInfos.get(i).getAppBasicInfoController().getTitle();
            else
                notInstalledAppList += ", " + appInfos.get(i).getAppBasicInfoController().getTitle();
        }
        return notInstalledAppList;
    }

    String getRequiredAppNotSetup() {
        String notInstalledAppList = "";
        ArrayList<AppInfoController> appInfos = applicationManager.getRequiredAppNotConfigured();
        if (appInfos.size() == 0) return null;
        for (int i = 0; i < appInfos.size(); i++) {
            if (i == 0)
                notInstalledAppList = appInfos.get(i).getAppBasicInfoController().getTitle();
            else
                notInstalledAppList += ", " + appInfos.get(i).getAppBasicInfoController().getTitle();
        }
        return notInstalledAppList;
    }
/*
        String notConfiguredAppList = "";
        int notConfiguredCount = 0;
        ArrayList<AppInfoController> appInfos = new ArrayList<>();
        for (int i = 0; i < applicationManager.getAppInfos().length; i++) {
            if (applicationManager.getAppInfos()[i].isRequired()) {
                if (!applicationManager.getAppInfos()[i].isInstalled()) {
                    notConfiguredCount++;
                    if (notConfiguredCount == 1)
                        notConfiguredAppList = applicationManager.getAppInfos()[i].getTitle();
                    else
                        notConfiguredAppList += ", " + applicationManager.getAppInfos()[i].getTitle();
                } else if (applicationManager.getAppInfos()[i].isMCerebrumSupported()
                        && applicationManager.getAppInfos()[i].getInfo() != null
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
*/

    BootstrapText getStatusText(boolean status) {
        if (status)
            return new BootstrapText.Builder(getContext()).addText("Status:   ").addFontAwesomeIcon("fa_check_circle")
                    .build();
        else
            return new BootstrapText.Builder(getContext()).addText("Status:   ").addFontAwesomeIcon("fa_times")
                    .build();
    }

}
