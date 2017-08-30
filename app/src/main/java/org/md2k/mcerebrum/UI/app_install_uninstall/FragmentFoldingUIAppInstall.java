package org.md2k.mcerebrum.UI.app_install_uninstall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ramotion.foldingcell.FoldingCell;

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
    public static final int INSTALL=0;
    public static final int UNINSTALL=1;
    public static final int UPDATE=2;
    Subscription subscription;
    MaterialDialog materialDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_folding_ui_app_install, parent, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        ListView theListView = (ListView) view.findViewById(R.id.listview_folding_ui);
        ApplicationManager applicationManager=new ApplicationManager();
        // prepare elements to display
        final ArrayList<Application> items = applicationManager.getAppList(getContext());
        ArrayList<AppInfo> appInfos=new ArrayList<>();
        for(int i=0;i<items.size();i++){
            appInfos.add(new AppInfo(getContext(), items.get(i)));
        }
        // add custom btn handler to first list item
/*
        items.get(0).setRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();
            }
        });
*/
        final FoldingCellListAdapterAppInstall adapter = new FoldingCellListAdapterAppInstall(getActivity(), appInfos, new ResponseCallBack() {
            @Override
            public void onResponse(int position, int operation) {
                if(operation==UNINSTALL){
                    items.get(position).uninstall(getActivity(), 1000);
                }else if(operation==INSTALL){
                    if(items.get(position).isInstallFromPlayStore(getActivity()))
                        items.get(position).installPlayStore(getActivity());
                    else {
                        downloadAndInstall(items.get(position));
                    }
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
    void downloadAndInstall(Application application){
        materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Downloading "+application.getTitle(getActivity())+" app...")
                .progress(false, 100, true)
                .show();
        subscription = application.installURL(getActivity())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
//                        Intent returnIntent = new Intent();
//                        returnIntent.putExtra("type", TYPE_GENERAL);
//                        setResult(Activity.RESULT_OK, returnIntent);
//                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(getActivity(), "Error: Download failed (e=" + e.getMessage() + ")").show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });

    }
    @Override
    public void onDestroy(){
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
        super.onDestroy();
    }

  }
