package org.md2k.mcerebrum.UI.app_install_uninstall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.ramotion.foldingcell.FoldingCell;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.Application;
import org.md2k.mcerebrum.app.ApplicationManager;
import org.md2k.mcerebrum.internet.download.DownloadInfo;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FragmentFoldingUIAppInstall extends Fragment {
    public static final int INSTALL = 0;
    public static final int UNINSTALL = 1;
    public static final int UPDATE = 2;
    Subscription subscription;
    MaterialDialog materialDialog;
    AwesomeTextView textViewInstalled;
    AwesomeTextView textViewUpdate;
    AwesomeTextView textViewNotInstalled;
    AwesomeTextView textViewStatus;
    BootstrapButton bootstrapButtonInstall;
    ApplicationManager applicationManager;
    FoldingCellListAdapterAppInstall adapter;
    int installAllIndex=-1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_folding_ui_app_install, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        ListView theListView = (ListView) view.findViewById(R.id.listview_folding_ui);
        installAllIndex=-1;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        getContext().registerReceiver(br, intentFilter);

        applicationManager = ((ActivityMain) getActivity()).applicationManager;
        // prepare elements to display
        textViewInstalled = (AwesomeTextView) view.findViewById(R.id.textview_installed);
        textViewUpdate = (AwesomeTextView) view.findViewById(R.id.textview_update);
        textViewNotInstalled = (AwesomeTextView) view.findViewById(R.id.textview_not_installed);
        textViewStatus = (AwesomeTextView) view.findViewById(R.id.textview_status);
        bootstrapButtonInstall = (BootstrapButton) view.findViewById(R.id.button_install);
        bootstrapButtonInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installAllIndex=0;
                downloadAndInstallAll();
            }
        });
        updateTextViewStatus();
//        String st="{fa_download}: "+installed[0]+"   {fa_refresh}: "+installed[1]+"   {fa_trash}: "+installed[2];
//        textViewInstalled.setBootstrapText(bootstrapText);
        // add custom btn handler to first list item
/*
        items.get(0).setRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();
            }
        });
*/
        adapter = new FoldingCellListAdapterAppInstall(getActivity(), applicationManager.getApplications(), new ResponseCallBack() {
            @Override
            public void onResponse(int position, int operation) {
                Application application=applicationManager.getApplications()[position];
                if (operation == UNINSTALL) {
                    application.uninstall(getActivity(), 1000);
                } else if (operation == INSTALL) {
                    if(application.getDownloadFromGithub()!=null || application.getDownloadFromURL()!=null){
                        downloadAndInstall(application);
                    }
                    else application.install(getActivity());
                }

            }
        });
        theListView.setAdapter(adapter);

        // set on click event listener to list view
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // toggle clicked cell state
                ((FoldingCell) view).toggle(false);
                // register in adapter that state for selected cell is toggled
                adapter.registerToggle(pos);
            }
        });
    }

    void downloadAndInstallAll() {
        if(installAllIndex==-1){
            return;
        }
        while(installAllIndex<applicationManager.getApplications().length && applicationManager.getApplications()[installAllIndex].isInstalled())
            installAllIndex++;
        if(installAllIndex>=applicationManager.getApplications().length) {
            installAllIndex = -1;
            return;
        }
        if(applicationManager.getApplications()[installAllIndex].getDownloadFromGithub()!=null || applicationManager.getApplications()[installAllIndex].getDownloadFromURL()!=null){
            downloadAndInstall(applicationManager.getApplications()[installAllIndex]);
        }
        else applicationManager.getApplications()[installAllIndex].install(getActivity());
    }

    void downloadAndInstall(final Application application) {
        materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Downloading "+application.getTitle()+" app...")
                .progress(false, 100, true)
                .show();
        subscription = application.download(getActivity())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        application.install(getActivity());
//                        Intent returnIntent = new Intent();
//                        returnIntent.putExtra("type", TYPE_GENERAL);
//                        setResult(Activity.RESULT_OK, returnIntent);
//                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(getActivity(), "Error: Download failed (e=" + e.toString() + ")").show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        Log.d("abc","total="+downloadInfo.getTotalFileSize()+"downloaded="+downloadInfo.getCurrentFileSize()+" progress="+downloadInfo.getProgress());
                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(br);
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
        super.onDestroy();
    }
    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_PACKAGE_ADDED:
//                case Intent.ACTION_PACKAGE_CHANGED:
//                case Intent.ACTION_PACKAGE_REPLACED:
                case Intent.ACTION_PACKAGE_REMOVED:
                    applicationManager.updateInfo();
                    adapter.notifyDataSetChanged();
                    updateTextViewStatus();
                    downloadAndInstallAll();
//                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentFoldingUIAppInstall()).commitAllowingStateLoss();
                    break;
            }
        }
    };
    void updateTextViewStatus(){
        int[] installed = applicationManager.getInstallStatus();
        BootstrapText bootstrapTextS;
        BootstrapText bootstrapTextI = new BootstrapText.Builder(getContext()).addFontAwesomeIcon("fa_download").addText(" : " + String.valueOf(installed[0])).build();
        BootstrapText bootstrapTextU = new BootstrapText.Builder(getContext()).addFontAwesomeIcon("fa_refresh").addText(" : " + String.valueOf(installed[1])).build();
        BootstrapText bootstrapTextD = new BootstrapText.Builder(getContext()).addFontAwesomeIcon("fa_trash").addText(" : " + String.valueOf(installed[2])).build();
        textViewInstalled.setBootstrapText(bootstrapTextI);
        textViewUpdate.setBootstrapText(bootstrapTextU);
        textViewNotInstalled.setBootstrapText(bootstrapTextD);
        if(applicationManager.isStatusOk()) {
            bootstrapTextS = new BootstrapText.Builder(getContext()).addText("Status: ").addFontAwesomeIcon("fa_check").build();
            textViewStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            textViewStatus.setBootstrapText(bootstrapTextS);
/*
            bootstrapButtonInstall.setEnabled(false);
            bootstrapButtonInstall.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
            bootstrapButtonInstall.setShowOutline(true);
*/
        } else {
            bootstrapTextS = new BootstrapText.Builder(getContext()).addText("Status: ").addFontAwesomeIcon("fa_times").build();
            textViewStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            textViewStatus.setBootstrapText(bootstrapTextS);
/*
            bootstrapButtonInstall.setEnabled(true);
            bootstrapButtonInstall.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            bootstrapButtonInstall.setShowOutline(false);
*/
        }
    }

}
