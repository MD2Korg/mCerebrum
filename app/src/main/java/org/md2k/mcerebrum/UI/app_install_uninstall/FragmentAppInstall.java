package org.md2k.mcerebrum.UI.app_install_uninstall;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.ramotion.foldingcell.FoldingCell;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.core.access.ActivityEmpty;
import org.md2k.mcerebrum.core.access.appinfo.AppAccess;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.constant.MCEREBRUM;
import org.md2k.mcerebrum.core.internet.download.DownloadInfo;
import org.md2k.mcerebrum.system.appinfo.AppInstall;
import org.md2k.mcerebrum.system.appinfo.BroadCastMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_CHECK_UPDATE;

public class FragmentAppInstall extends Fragment {
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
    BootstrapButton bootstrapButtonUpdate;
    CellAppInstall adapter;
    HashMap<String, Drawable> icons;
    int installAllIndex = -1;
    boolean isUpdateUI = false;
    ArrayList<String> packageNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folding_ui_app_install, parent, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppInstall.set(MyApplication.getContext());
        AppAccess.set(MyApplication.getContext());
        LocalBroadcastManager.getInstance(MyApplication.getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(MCEREBRUM.APP_ACCESS.APPCP_CHANGED));
/*
        if(!isUpdateUI && installAllIndex!=-1){
            materialDialog.show();
        }
*/
        if (isUpdateUI) {
            isUpdateUI = false;
            downloadAndInstallAll();
        }

    }
    @Override
    public void onPause(){
        super.onPause();

        if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        if(materialDialog!=null && materialDialog.isShowing()){
            materialDialog.dismiss();
        }
        isUpdateUI=true;
        LocalBroadcastManager.getInstance(MyApplication.getContext()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        ListView theListView = (ListView) view.findViewById(R.id.listview_folding_ui);
        installAllIndex = -1;
        icons=new HashMap<>();
        packageNames = AppBasicInfo.get(MyApplication.getContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        getContext().registerReceiver(br, intentFilter);

        // prepare elements to display
        textViewInstalled = (AwesomeTextView) view.findViewById(R.id.textview_installed);
        textViewUpdate = (AwesomeTextView) view.findViewById(R.id.textview_update);
        textViewNotInstalled = (AwesomeTextView) view.findViewById(R.id.textview_not_installed);
        textViewStatus = (AwesomeTextView) view.findViewById(R.id.textview_status);
        bootstrapButtonInstall = (BootstrapButton) view.findViewById(R.id.button_install);
        bootstrapButtonInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installAllIndex = 0;
                downloadAndInstallAll();
            }
        });
        bootstrapButtonUpdate = (BootstrapButton) view.findViewById(R.id.button_update);

        bootstrapButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MENU_CHECK_UPDATE);
            }
        });

        updateTextViewStatus();
        adapter = new CellAppInstall(FragmentAppInstall.this, packageNames, new ResponseCallBack() {
            @Override
            public void onResponse(int position, int operation) {
                if (operation == UNINSTALL)
                    AppInstall.uninstall(getActivity(), packageNames.get(position), 1000);
                else
                    install(packageNames.get(position));

            }
        });
        theListView.setAdapter(adapter);

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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTextViewStatus();
            adapter.notifyDataSetChanged();
        }
    };

    void installMCerebrumIfRequired() {
        String packageName = AppBasicInfo.getMCerebrum(MyApplication.getContext());
        if(!AppInstall.hasUpdate(MyApplication.getContext(), packageName)){
            installAllIndex = -1;
            BroadCastMessage.send(getActivity(), MCEREBRUM.APP_ACCESS.OP_DATAKIT_STOP);
            return;
        }
        install(packageName);
    }

    void downloadAndInstallAll() {
        if (installAllIndex == -1) {
            return;
        }
        installAllIndex=0;
        while (installAllIndex < packageNames.size()
                && ((AppInstall.getInstalled(MyApplication.getContext(), packageNames.get(installAllIndex))
                && !AppInstall.hasUpdate(MyApplication.getContext(), packageNames.get(installAllIndex)))
                || MCEREBRUM.APP.TYPE_MCEREBRUM.equalsIgnoreCase(AppBasicInfo.getType(MyApplication.getContext(), packageNames.get(installAllIndex)))))
            installAllIndex++;
        if (installAllIndex >= packageNames.size()) {
            installMCerebrumIfRequired();
            return;
        }
        install(packageNames.get(installAllIndex));
        installAllIndex++;
    }

    void install(String packageName) {

        materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Downloading " + AppBasicInfo.getTitle(MyApplication.getContext(), packageName) + " ...")
                .progress(false, 100, true)
                .autoDismiss(false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        installAllIndex=-1;
                        if(subscription!=null && !subscription.isUnsubscribed())
                            subscription.unsubscribe();
                        if(materialDialog!=null && materialDialog.isShowing()) materialDialog.dismiss();
                    }
                })
                .show();
        subscription = AppInstall.install(getActivity(), packageName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
//                        BroadCastMessage.send(MyApplication.getContext());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(getActivity(), "Error: Download failed (e=" + e.toString() + ")").show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        Log.d("abc", "total=" + downloadInfo.getTotalFileSize() + "downloaded=" + downloadInfo.getCurrentFileSize() + " progressWithBar=" + downloadInfo.getProgress());
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
                case Intent.ACTION_PACKAGE_CHANGED:
                case Intent.ACTION_PACKAGE_REPLACED:
                case Intent.ACTION_PACKAGE_REMOVED:
                    String[] temp = intent.getData().toString().split(":");
                    String packageName;
                    if (temp.length == 1)
                        packageName = temp[0];
                    else if (temp.length == 2)
                        packageName = temp[1];
                    else packageName = "";
                    AppInstall.set(context, packageName);
                    adapter.notifyDataSetChanged();
                    updateTextViewStatus();
                    try {
                        Intent i = new Intent();
                        i.setComponent(new ComponentName(packageName, "org.md2k.mcerebrum.core.access.ActivityEmpty"));
                        startActivity(i);
                    }catch(Exception e){
                    }
                    if(installAllIndex==-1)
                        BroadCastMessage.send(getActivity(), MCEREBRUM.APP_ACCESS.OP_DATAKIT_STOP);
                    isUpdateUI = true;


//                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentFoldingUIAppInstall()).commitAllowingStateLoss();
                    break;
            }
        }
    };

    void updateTextViewStatus() {
        try {
            int[] installed = AppInstall.getInstallStatus(MyApplication.getContext());
            BootstrapText bootstrapTextS;
            BootstrapText bootstrapTextI = new BootstrapText.Builder(MyApplication.getContext()).addFontAwesomeIcon("fa_download").addText(" : " + String.valueOf(installed[0])).build();
            BootstrapText bootstrapTextU = new BootstrapText.Builder(MyApplication.getContext()).addFontAwesomeIcon("fa_refresh").addText(" : " + String.valueOf(installed[1])).build();
            BootstrapText bootstrapTextD = new BootstrapText.Builder(MyApplication.getContext()).addFontAwesomeIcon("fa_trash").addText(" : " + String.valueOf(installed[2])).build();
            textViewInstalled.setBootstrapText(bootstrapTextI);
            textViewUpdate.setBootstrapText(bootstrapTextU);
            textViewNotInstalled.setBootstrapText(bootstrapTextD);
            if (AppInstall.isRequiredAppInstalled(MyApplication.getContext())) {
                bootstrapTextS = new BootstrapText.Builder(MyApplication.getContext()).addText("Status: ").addFontAwesomeIcon("fa_check").build();
                textViewStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                textViewStatus.setBootstrapText(bootstrapTextS);
/*
            bootstrapButtonInstall.setEnabled(false);
            bootstrapButtonInstall.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
            bootstrapButtonInstall.setShowOutline(true);
*/
            } else {
                bootstrapTextS = new BootstrapText.Builder(MyApplication.getContext()).addText("Status: ").addFontAwesomeIcon("fa_times").build();
                textViewStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                textViewStatus.setBootstrapText(bootstrapTextS);
/*
            bootstrapButtonInstall.setEnabled(true);
            bootstrapButtonInstall.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            bootstrapButtonInstall.setShowOutline(false);
*/
            }
        }catch(Exception e){}
    }

}
