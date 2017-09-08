package org.md2k.mcerebrum.UI.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.Application;
import org.md2k.mcerebrum.app.ApplicationManager;
import org.md2k.mcerebrum.data.StudyInfo;
import org.md2k.mcerebrum.data.UserInfo;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class FragmentHome extends Fragment {
    ApplicationManager applicationManager;
    StudyInfo studyInfo;
    UserInfo userInfo;

    AwesomeTextView awesomeTextViewSummary;
    AwesomeTextView awesomeTextViewInstall;
    AwesomeTextView awesomeTextViewSetup;
    BootstrapButton bootstrapButtonStart;
    AwesomeTextView awesomeTextViewInstallStatus;
    AwesomeTextView awesomeTextViewSetupStatus;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_home, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        applicationManager = ((ActivityMain) getActivity()).applicationManager;
        studyInfo = ((ActivityMain) getActivity()).studyInfo;
        userInfo = ((ActivityMain) getActivity()).userInfo;
        awesomeTextViewSummary = (AwesomeTextView) view.findViewById(R.id.awesome_textview_summary);
        awesomeTextViewInstall = (AwesomeTextView) view.findViewById(R.id.awesome_textview_install);
        awesomeTextViewInstallStatus = (AwesomeTextView) view.findViewById(R.id.awesome_textview_install_status);
        awesomeTextViewSetupStatus = (AwesomeTextView) view.findViewById(R.id.awesome_textview_setup_status);
        awesomeTextViewSetup = (AwesomeTextView) view.findViewById(R.id.awesome_textview_setup);
        bootstrapButtonStart = (BootstrapButton) view.findViewById(R.id.button_bootstrap_start);
        bootstrapButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application application = applicationManager.getStudy();
                if(application!=null) application.launch(getActivity());
                else Toasty.error(getContext(),"Study App not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        updateSummary();
        boolean resInstall = updateInstall();
        boolean resUpdate = updateSetup();

        if (!resInstall || !resUpdate) {
            bootstrapButtonStart.setEnabled(false);
            bootstrapButtonStart.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
            bootstrapButtonStart.setShowOutline(true);
        } else {
            bootstrapButtonStart.setEnabled(true);
            bootstrapButtonStart.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            bootstrapButtonStart.setShowOutline(false);
        }
    }

    void updateSummary() {
        awesomeTextViewSummary.setBootstrapText(getSummary());
    }

    boolean updateInstall() {
        String notInstalledApp = getInstall();
        BootstrapText bt;
        if (notInstalledApp == null) {
            bt = new BootstrapText.Builder(getContext()).addText("Applications are properly installed")
                    .build();
            awesomeTextViewInstall.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewInstallStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewInstallStatus.setBootstrapText(getStatusText(true));
            awesomeTextViewInstall.setBootstrapText(bt);
            return true;
        } else {
            bt = new BootstrapText.Builder(getContext()).addText("Please install: " + notInstalledApp)
                    .build();
            awesomeTextViewInstall.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewInstallStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewInstallStatus.setBootstrapText(getStatusText(false));
            awesomeTextViewInstall.setBootstrapText(bt);
            return false;
        }
    }

    boolean updateSetup() {
        String notConfiguredApp = getSetup();
        BootstrapText bt;
        if (notConfiguredApp == null) {
            bt = new BootstrapText.Builder(getContext()).addText("Applications are properly configured.")
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
        return new BootstrapText.Builder(getContext()).addText("\tStudy Title:   " + studyInfo.getTitle() + "\t\t\t\tUser id:   " + userInfo.getTitle())
                .build();
    }

    String getInstall() {
        String notInstalledAppList = null;
        ArrayList<Application> apps = applicationManager.getRequiredAppNotInstalled();
        if(apps.size()==0) return null;
        for (int i = 0; i < apps.size(); i++) {
            if (applicationManager.getApplications()[i].isRequired()) {
                    if (i == 0)
                        notInstalledAppList = apps.get(i).getTitle();
                    else
                        notInstalledAppList += ", " + apps.get(i).getTitle();
            }
        }
        return notInstalledAppList;
    }

    String getSetup() {
        String notConfiguredAppList = null;
        int notConfiguredCount = 0;
        for (int i = 0; i < applicationManager.getApplications().length; i++) {
            if (applicationManager.getApplications()[i].isRequired()) {
                if (!applicationManager.getApplications()[i].isInstalled()) {
                    notConfiguredCount++;
                    if (notConfiguredCount == 1)
                        notConfiguredAppList = applicationManager.getApplications()[i].getTitle();
                    else
                        notConfiguredAppList += ", " + applicationManager.getApplications()[i].getTitle();
                } else if (applicationManager.getApplications()[i].isConfigurable() && !applicationManager.getApplications()[i].isConfigured()) {
                    notConfiguredCount++;
                    if (notConfiguredCount == 1)
                        notConfiguredAppList = applicationManager.getApplications()[i].getTitle();
                    else
                        notConfiguredAppList += ", " + applicationManager.getApplications()[i].getTitle();
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
