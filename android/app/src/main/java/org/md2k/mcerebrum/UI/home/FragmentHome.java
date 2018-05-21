package org.md2k.mcerebrum.UI.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.config_info.ConfigInfo;
import org.md2k.mcerebrum.core.access.appinfo.AppAccess;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.constant.MCEREBRUM;
import org.md2k.mcerebrum.study_info.StudyInfo;
import org.md2k.mcerebrum.system.appinfo.AppInstall;
import org.md2k.mcerebrum.system.appinfo.BroadCastMessage;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_APP_ADD_REMOVE;
import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_APP_SETTINGS;
import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_JOIN;

public class FragmentHome extends Fragment {
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
            if(ConfigInfo.getType().equalsIgnoreCase(MCEREBRUM.CONFIG.TYPE_FREEBIE))
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
            ((LinearLayout) view.findViewById(R.id.join1)).setBackgroundColor(ContextCompat.getColor(MyApplication.getContext(), R.color.md_red_900));
        }
        else if(dataManager.getDataCPManager().getStudyCP().getType().equalsIgnoreCase(STUDY.SERVER)){
            ((LinearLayout) view.findViewById(R.id.join1)).setBackgroundColor(ContextCompat.getColor(MyApplication.getContext(), R.color.md_cyan_900));
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
                ArrayList<String> packageNames = AppBasicInfo.getStudy(MyApplication.getContext());
                if (packageNames==null || packageNames.size() == 0 || !AppInstall.isCoreInstalled(MyApplication.getContext())) {
                    Toasty.error(MyApplication.getContext(), "Datakit/study is not installed", Toast.LENGTH_SHORT).show();
                    StudyInfo.setStarted(false);
                } else {
                    StudyInfo.setStarted(true);
                    AppAccess.launch(MyApplication.getContext(), packageNames.get(0));
                    getActivity().finish();
                }
            }
        });

        textViewClickInstall = (TextView) view.findViewById(R.id.textview_install);
        textViewClickSetup = (TextView) view.findViewById(R.id.textview_setup);
        textViewClickJoin = (TextView) view.findViewById(R.id.textview_join);
        ((ActivityMain)getActivity()).initStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppInstall.set(MyApplication.getContext());
        AppAccess.set(MyApplication.getContext());
        LocalBroadcastManager.getInstance(MyApplication.getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(MCEREBRUM.APP_ACCESS.APPCP_CHANGED));

        BroadCastMessage.send(MyApplication.getContext(), MCEREBRUM.APP_ACCESS.OP_DATAKIT_STOP);
        updateSummary();
        updateInstall();
        updateSetup();
        updateButton();
    }
    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(MyApplication.getContext()).unregisterReceiver(mMessageReceiver);
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSummary();
            updateInstall();
            updateSetup();
            updateButton();
        }
    };

    void updateButton(){
        if (!AppInstall.isCoreInstalled(MyApplication.getContext())) {
            bootstrapButtonStart.setEnabled(false);
            bootstrapButtonStart.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            bootstrapButtonStart.setBootstrapText(new BootstrapText.Builder(MyApplication.getContext()).addFontAwesomeIcon("fa-ban").addText("  Start Study").build());
            bootstrapButtonStart.setShowOutline(true);
        } else {
            bootstrapButtonStart.setEnabled(true);
            bootstrapButtonStart.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            bootstrapButtonStart.setBootstrapText(new BootstrapText.Builder(MyApplication.getContext()).addFontAwesomeIcon("fa-play-circle-o").addText("  Start Study").build());
            bootstrapButtonStart.setShowOutline(false);
        }

    }

    void updateSummary() {
        awesomeTextViewSummary.setBootstrapText(getSummary());
        String type = ConfigInfo.getType();
        if(type==null || type.equalsIgnoreCase(MCEREBRUM.CONFIG.TYPE_FREEBIE)) {
            textViewClickJoin.setVisibility(View.VISIBLE);
            awesomeTextViewSummary.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
        }
        else {
            awesomeTextViewSummary.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            textViewClickJoin.setVisibility(View.INVISIBLE);
        }

    }

    void updateInstall() {
        ArrayList<String> appInfos = AppInstall.getRequiredAppNotInstalled(MyApplication.getContext());
//        String notInstalledApp = getRequiredAppNotInstalled();
        BootstrapText bt;
        if (appInfos.size()==0) {
            bt = new BootstrapText.Builder(MyApplication.getContext()).addText("Installation Successful")
                    .build();
            awesomeTextViewInstall.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewInstallStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewInstallStatus.setBootstrapText(getStatusText(true));
            awesomeTextViewInstall.setBootstrapText(bt);
        } else {
            bt = new BootstrapText.Builder(MyApplication.getContext()).addText("Not installed: " + appInfos.size()+" apps")
                    .build();
            awesomeTextViewInstall.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewInstallStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewInstallStatus.setBootstrapText(getStatusText(false));
            awesomeTextViewInstall.setBootstrapText(bt);
        }
    }

    boolean updateSetup() {
        ArrayList<String> appInfos = AppAccess.getRequiredAppNotConfigured(MyApplication.getContext());
//        String notConfiguredApp = getRequiredAppNotSetup();
        BootstrapText bt;
        if (appInfos.size() == 0) {
            bt = new BootstrapText.Builder(MyApplication.getContext()).addText("Application Setup Successful")
                    .build();
            awesomeTextViewSetup.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewSetupStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewSetupStatus.setBootstrapText(getStatusText(true));
            awesomeTextViewSetup.setBootstrapText(bt);
            return true;
        } else {
            bt = new BootstrapText.Builder(MyApplication.getContext()).addText("Not Configured: " + appInfos.size()+" apps")
                    .build();
            awesomeTextViewSetup.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewSetupStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewSetupStatus.setBootstrapText(getStatusText(false));
            awesomeTextViewSetup.setBootstrapText(bt);
            return false;
        }
    }

    BootstrapText getSummary() {
        String name=StudyInfo.getTitle();
        String v = ConfigInfo.getVersion();
        if(v==null) v="2.0.0";
        if(name==null || name.equalsIgnoreCase(MCEREBRUM.CONFIG.TYPE_FREEBIE))
        return new BootstrapText.Builder(MyApplication.getContext()).addText("General Use ("+v+")")
                .build();
        else
        return new BootstrapText.Builder(MyApplication.getContext()).addText(StudyInfo.getTitle()+" ("+v+")")
                .build();
    }

    String getRequiredAppNotInstalled() {
        String notInstalledAppList = "";
        ArrayList<String> appInfos = AppInstall.getRequiredAppNotInstalled(MyApplication.getContext());
        if (appInfos.size() == 0) return null;
        for (int i = 0; i < appInfos.size(); i++) {
            if (i == 0)
                notInstalledAppList = AppBasicInfo.getTitle(MyApplication.getContext(), appInfos.get(i));
            else
                notInstalledAppList += ", " + AppBasicInfo.getTitle(MyApplication.getContext(), appInfos.get(i));
        }
        return notInstalledAppList;
    }

    String getRequiredAppNotSetup() {
        String notInstalledAppList = "";

        ArrayList<String> appInfos = AppAccess.getRequiredAppNotConfigured(MyApplication.getContext());
        if (appInfos.size() == 0) return null;
        for (int i = 0; i < appInfos.size(); i++) {
            if (i == 0)
                notInstalledAppList = AppBasicInfo.getTitle(MyApplication.getContext(), appInfos.get(i));
            else
                notInstalledAppList += ", " + AppBasicInfo.getTitle(MyApplication.getContext(), appInfos.get(i));
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
            return new BootstrapText.Builder(MyApplication.getContext()).addText("Status:   ").addFontAwesomeIcon("fa_check_circle")
                    .build();
        else
            return new BootstrapText.Builder(MyApplication.getContext()).addText("Status:   ").addFontAwesomeIcon("fa_times")
                    .build();
    }

}
