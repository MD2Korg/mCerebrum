package org.md2k.mcerebrum.UI.check_update;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.ApplicationManager;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.data.DataManager;
import org.md2k.system.internet.download.DownloadInfo;
import org.md2k.system.provider.UserCP;

import es.dmoral.toasty.Toasty;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_APP_ADD_REMOVE;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FragmentCheckUpdate extends Fragment {
    Subscription subscription;
    Subscription subscriptionApp;

    MaterialDialog materialDialog;
    DataManager dataManager;
    ApplicationManager applicationManager;
    ActivityMain activityMain;
    AwesomeTextView awesomeTextViewSummary;
    AwesomeTextView awesomeTextViewInstall;
    AwesomeTextView awesomeTextViewSummaryStatus;
    AwesomeTextView awesomeTextViewInstallStatus;
    TextView textViewClickInstall;
    TextView textViewClickJoin;
    int count;
    int countUpdate;
    View.OnClickListener onClickListernerInstall = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MENU_APP_ADD_REMOVE);
        }
    };
    View.OnClickListener onClickListernerJoin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (dataManager.getDataCPManager().getConfigCP().getConfigInfoBean().getVersions() == null || dataManager.getDataCPManager().getConfigCP().getConfigInfoBean().getVersions().equalsIgnoreCase(dataManager.getDataCPManager().getConfigCP().getConfigInfoBean().getLatestVersion()))
                return;
            materialDialog = Dialog.progressIndeterminate(getActivity(), "Downloading new configuration file...").show();
            final UserCP userCP = dataManager.getDataCPManager().getUserCP();
            subscription = Observable.just(true).subscribeOn(Schedulers.newThread()).flatMap(new Func1<Boolean, Observable<DownloadInfo>>() {
                @Override
                public Observable<DownloadInfo> call(Boolean aBoolean) {

                    return dataManager.getDataFileManager()
                            .downloadAndExtractFromServer(dataManager.getDataCPManager().getConfigCP().getConfigInfoBean().getDownloadLink(), userCP.getTitle(), userCP.getPasswordHash());
                }
            }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<DownloadInfo>() {
                        @Override
                        public void onCompleted() {
                            materialDialog.dismiss();
                            dataManager.updateDataDP();
                            Toasty.success(getActivity(), "Successfully Logged in", Toast.LENGTH_SHORT).show();
                            activityMain.updateUI();

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toasty.error(getContext(), e.getMessage()).show();
                            materialDialog.dismiss();

                        }

                        @Override
                        public void onNext(DownloadInfo downloadInfo) {

                        }
                    });

/*
            if(dataManager.getDataCPManager().getConfigCP().getType().equalsIgnoreCase(MCEREBRUM.CONFIG.TYPE_FREEBIE))
                ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MENU_JOIN);
*/
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_check_update, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        prepareClick(view);
        activityMain = (ActivityMain) getActivity();
        dataManager = activityMain.dataManager;
        applicationManager = activityMain.applicationManager;
        count = 0;
        countUpdate = 0;
        materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Checking updates ...")
                .progress(true, 100, false)
                .show();

        subscription = dataManager.checkUpdateConfig().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                count++;
                if (count >= 2)
                    materialDialog.dismiss();
                updateSummary();
                Log.d("abc", "count=" + count);
            }

            @Override
            public void onError(Throwable e) {
                materialDialog.dismiss();
            }

            @Override
            public void onNext(Boolean aBoolean) {
            }
        });
        subscriptionApp = applicationManager.checkUpdate().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer() {
            @Override
            public void onCompleted() {
                count++;
                if (count >= 2)
                    materialDialog.dismiss();
                Log.d("abc", "count=" + count);
                updateInstall();

            }

            @Override
            public void onError(Throwable e) {
                materialDialog.dismiss();
            }

            @Override
            public void onNext(Object o) {
                Boolean b = (Boolean) o;
                if (b == true) countUpdate++;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (materialDialog != null && materialDialog.isShowing()) materialDialog.dismiss();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        if (subscriptionApp != null && !subscriptionApp.isUnsubscribed())
            subscriptionApp.unsubscribe();
        super.onDestroy();
    }

    void prepareClick(View view) {
        view.findViewById(R.id.join1).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join2).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join3).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join4).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join5).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join6).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.join7).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.awesome_textview_summary).setOnClickListener(onClickListernerJoin);
        view.findViewById(R.id.textview_join).setOnClickListener(onClickListernerJoin);

        view.findViewById(R.id.app_install1).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.app_install2).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.app_install3).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.app_install4).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.app_install5).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.awesome_textview_install_status).setOnClickListener(onClickListernerInstall);
        view.findViewById(R.id.awesome_textview_install).setOnClickListener(onClickListernerInstall);

        awesomeTextViewSummaryStatus = (AwesomeTextView) view.findViewById(R.id.join6);
        awesomeTextViewSummary = (AwesomeTextView) view.findViewById(R.id.awesome_textview_summary);
        awesomeTextViewInstall = (AwesomeTextView) view.findViewById(R.id.awesome_textview_install);
        awesomeTextViewInstallStatus = (AwesomeTextView) view.findViewById(R.id.awesome_textview_install_status);

        textViewClickInstall = (TextView) view.findViewById(R.id.textview_install);
        textViewClickJoin = (TextView) view.findViewById(R.id.textview_join);
    }

    void updateSummary() {
        if (dataManager.getDataCPManager().getConfigCP().getConfigInfoBean().getVersions() == null || dataManager.getDataCPManager().getConfigCP().getConfigInfoBean().getVersions().equalsIgnoreCase(dataManager.getDataCPManager().getConfigCP().getConfigInfoBean().getLatestVersion())) {
            awesomeTextViewSummary.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewSummary.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("up-to-date").build());
            textViewClickJoin.setVisibility(View.INVISIBLE);
            awesomeTextViewSummaryStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewSummaryStatus.setBootstrapText(getStatusText(true));
        } else {
            awesomeTextViewSummary.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
            awesomeTextViewSummary.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("update available").build());
            textViewClickJoin.setVisibility(View.VISIBLE);
            awesomeTextViewSummaryStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewSummaryStatus.setBootstrapText(getStatusText(false));
        }
    }

    void updateInstall() {
        BootstrapText bt;
        if (countUpdate == 0) {
            bt = new BootstrapText.Builder(getContext()).addText("up-to-date")
                    .build();
            awesomeTextViewInstall.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewInstallStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            awesomeTextViewInstallStatus.setBootstrapText(getStatusText(true));
            awesomeTextViewInstall.setBootstrapText(bt);
        } else {
            bt = new BootstrapText.Builder(getContext()).addText("Update available: (App: " + countUpdate + " )")
                    .build();
            awesomeTextViewInstall.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewInstallStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            awesomeTextViewInstallStatus.setBootstrapText(getStatusText(false));
            awesomeTextViewInstall.setBootstrapText(bt);
        }
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
