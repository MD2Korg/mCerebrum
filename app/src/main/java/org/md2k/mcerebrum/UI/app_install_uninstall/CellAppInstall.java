package org.md2k.mcerebrum.UI.app_install_uninstall;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.ramotion.foldingcell.FoldingCell;

import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.constant.MCEREBRUM;
import org.md2k.mcerebrum.system.appinfo.AppInstall;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */
class CellAppInstall extends ArrayAdapter<String> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;
    private ResponseCallBack responseCallBack;
    FragmentAppInstall f;


    CellAppInstall(FragmentAppInstall activity, ArrayList<String> objects, ResponseCallBack responseCallBack) {
        super(activity.getContext(), 0, objects);
        f=activity;
        this.responseCallBack=responseCallBack;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // get item for selected view
        String packageName=getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell_app_install, parent, false);
            viewHolder.title = (TextView) cell.findViewById(R.id.textview_title);
            viewHolder.summary = (TextView) cell.findViewById(R.id.textview_description);
            viewHolder.icon_short = (ImageView) cell.findViewById(R.id.imageview_icon_short);
            viewHolder.icon_long = (ImageView) cell.findViewById(R.id.imageview_icon_long);

            viewHolder.content_title = (TextView) cell.findViewById(R.id.textview_content_title);
            viewHolder.content_summary = (TextView) cell.findViewById(R.id.textview_content_description);
            viewHolder.description =(TextView)cell.findViewById(R.id.textview_content_detail);
            viewHolder.version=(TextView)cell.findViewById(R.id.textview_version_number);
            viewHolder.updateVersion =(TextView)cell.findViewById(R.id.textview_update_date);

            viewHolder.buttonInstallLong =(BootstrapButton)cell.findViewById(R.id.button_install_long);
            viewHolder.buttonUpdateLong =(BootstrapButton)cell.findViewById(R.id.button_update_long);
            viewHolder.buttonUninstallLong =(BootstrapButton)cell.findViewById(R.id.button_uninstall_long);
            viewHolder.buttonInstallShort =(BootstrapButton)cell.findViewById(R.id.button_install_short);
            viewHolder.buttonUpdateShort =(BootstrapButton)cell.findViewById(R.id.button_update_short);
            viewHolder.buttonUninstallShort =(BootstrapButton)cell.findViewById(R.id.button_uninstall_short);
            //viewHolder.status= (AwesomeTextView) cell.findViewById(R.id.textview_status);

            viewHolder.status = (AwesomeTextView) cell.findViewById(R.id.textview_status);

            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }
        // bind data from selected element to view through view holder
        String useAs=AppBasicInfo.getUseAs(getContext(), packageName);
        if(useAs!=null && useAs.equalsIgnoreCase(MCEREBRUM.APP.USE_AS_REQUIRED))
            viewHolder.title.setText(AppBasicInfo.getTitle(getContext(), packageName)+" (Required)");
        else
            viewHolder.title.setText(AppBasicInfo.getTitle(getContext(), packageName));
        viewHolder.summary.setText(AppBasicInfo.getSummary(getContext(), packageName));
        viewHolder.content_title.setText(AppBasicInfo.getTitle(getContext(), packageName));
        viewHolder.content_summary.setText(AppBasicInfo.getSummary(getContext(), packageName));
        viewHolder.description.setText(AppBasicInfo.getDescription(getContext(), packageName));
        String versionName= AppInstall.getCurrentVersion(getContext(), packageName);
        viewHolder.version.setText(versionName);
        String lastVersionName= AppInstall.getLatestVersion(getContext(), packageName);
        viewHolder.updateVersion.setText(lastVersionName);
        Drawable d;
        if(f.icons.containsKey(packageName))
            d=f.icons.get(packageName);
        else d = AppBasicInfo.getIcon(getContext(), packageName, Constants.CONFIG_MCEREBRUM_DIR());
        viewHolder.icon_short.setImageDrawable(d);
        viewHolder.icon_long.setImageDrawable(d);

        View.OnClickListener onClickListenerUninstall=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentAppInstall.UNINSTALL);
            }
        };
        View.OnClickListener onClickListenerInstall=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentAppInstall.INSTALL);
            }
        };
        View.OnClickListener onClickListenerUpdate = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentAppInstall.UPDATE);
            }
        };
        boolean isInstalled = AppInstall.getInstalled(getContext(), packageName);
        if(isInstalled && !f.icons.containsKey(packageName))
            f.icons.put(packageName, AppBasicInfo.getIcon(getContext(), packageName, Constants.CONFIG_MCEREBRUM_DIR()));
        boolean hasUpdate = AppInstall.hasUpdate(getContext(), packageName);

        if(AppBasicInfo.getMCerebrum(getContext()).equals(packageName)){
            set(viewHolder.buttonInstallLong, viewHolder.buttonInstallShort, false, DefaultBootstrapBrand.SECONDARY, true, onClickListenerInstall);
            set(viewHolder.buttonUninstallLong, viewHolder.buttonUninstallShort, false, DefaultBootstrapBrand.SECONDARY, true, onClickListenerUninstall);
        }
        else if(isInstalled) {
            set(viewHolder.buttonInstallLong, viewHolder.buttonInstallShort, false, DefaultBootstrapBrand.SECONDARY, true, onClickListenerInstall);
            set(viewHolder.buttonUninstallLong, viewHolder.buttonUninstallShort, true, DefaultBootstrapBrand.DANGER, true, onClickListenerUninstall);
        }
        else {
            set(viewHolder.buttonInstallLong, viewHolder.buttonInstallShort, true, DefaultBootstrapBrand.SUCCESS, false,onClickListenerInstall);
            set(viewHolder.buttonUninstallLong, viewHolder.buttonUninstallShort, false, DefaultBootstrapBrand.SECONDARY, true, onClickListenerUninstall);
       }
        if(isInstalled && hasUpdate) {
            set(viewHolder.buttonUpdateLong, viewHolder.buttonUpdateShort, true, DefaultBootstrapBrand.WARNING, false, onClickListenerUpdate);
        }
        else{
            set(viewHolder.buttonUpdateLong, viewHolder.buttonUpdateShort, false, DefaultBootstrapBrand.SECONDARY, true,onClickListenerUpdate);
        }

        if(!isInstalled){
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            viewHolder.status.setText("not installed");
        }else if(hasUpdate){
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
            viewHolder.status.setText("update available");
        }else{
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            viewHolder.status.setText("installed");
        }

        return cell;
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    public View.OnClickListener getDefaultRequestBtnClickListener() {
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(View.OnClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
    }
    void set(BootstrapButton b1, BootstrapButton b2, boolean e, BootstrapBrand b, boolean o, View.OnClickListener l){
        b1.setEnabled(e);
        b1.setBootstrapBrand(b);
        b1.setShowOutline(o);
        b1.setOnClickListener(l);
        b2.setEnabled(e);
        b2.setBootstrapBrand(b);
        b2.setShowOutline(o);
        b2.setOnClickListener(l);
    }

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView summary;
        AwesomeTextView status;
        ImageView icon_short;
        ImageView icon_long;
        TextView content_title;
        TextView content_summary;
        TextView description;
        TextView version;
        TextView updateVersion;
        BootstrapButton buttonInstallLong;
        BootstrapButton buttonUninstallLong;
        BootstrapButton buttonUpdateLong;
        BootstrapButton buttonInstallShort;
        BootstrapButton buttonUninstallShort;
        BootstrapButton buttonUpdateShort;

    }
}
