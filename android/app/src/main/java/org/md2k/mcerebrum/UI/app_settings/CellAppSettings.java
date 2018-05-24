package org.md2k.mcerebrum.UI.app_settings;

import android.content.Context;
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
import org.md2k.mcerebrum.MainApplication;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.core.access.appinfo.AppAccess;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.constant.MCEREBRUM;
import org.md2k.mcerebrum.system.appinfo.AppInstall;

import java.util.HashSet;
import java.util.List;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */
public class CellAppSettings extends ArrayAdapter<String> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;
    private ResponseCallBack responseCallBack;


    CellAppSettings(Context context, List<String> packageNames, ResponseCallBack responseCallBack) {
        super(context, 0, packageNames);
        this.responseCallBack = responseCallBack;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // get item for selected view
        String packageName = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(MainApplication.getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell_app_settings, parent, false);
            viewHolder.title = (TextView) cell.findViewById(R.id.textview_title);
            viewHolder.summary = (TextView) cell.findViewById(R.id.textview_description);
            viewHolder.icon_short = (ImageView) cell.findViewById(R.id.imageview_icon_short);
            viewHolder.icon_long = (ImageView) cell.findViewById(R.id.imageview_icon_long);

            viewHolder.content_title = (TextView) cell.findViewById(R.id.textview_content_title);
            viewHolder.content_summary = (TextView) cell.findViewById(R.id.textview_content_description);
            viewHolder.description = (TextView) cell.findViewById(R.id.textview_content_detail);
            viewHolder.version = (TextView) cell.findViewById(R.id.textview_version_number);
            viewHolder.updateVersion = (TextView) cell.findViewById(R.id.textview_update_date);

            viewHolder.buttonSettingsLong = (BootstrapButton) cell.findViewById(R.id.button_settings_long);
            viewHolder.buttonSettingsShort = (BootstrapButton) cell.findViewById(R.id.button_settings_short);
            viewHolder.buttonLaunchLong = (BootstrapButton) cell.findViewById(R.id.button_launch_long);
            viewHolder.buttonLaunchShort = (BootstrapButton) cell.findViewById(R.id.button_launch_short);
            viewHolder.buttonClearLong = (BootstrapButton) cell.findViewById(R.id.button_clear_long);
            viewHolder.buttonClearShort = (BootstrapButton) cell.findViewById(R.id.button_clear_short);
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
        viewHolder.title.setText(AppBasicInfo.getTitle(MainApplication.getContext(), packageName));
        viewHolder.summary.setText(AppBasicInfo.getSummary(MainApplication.getContext(), packageName));
        viewHolder.content_title.setText(AppBasicInfo.getTitle(MainApplication.getContext(), packageName));
        viewHolder.content_summary.setText(AppBasicInfo.getSummary(MainApplication.getContext(), packageName));
        viewHolder.description.setText(AppBasicInfo.getDescription(MainApplication.getContext(), packageName));
        String versionName= AppInstall.getCurrentVersion(MainApplication.getContext(), packageName);
        viewHolder.version.setText(versionName);
        String lastVersionName= AppInstall.getLatestVersion(MainApplication.getContext(), packageName);
        viewHolder.updateVersion.setText(lastVersionName);

        viewHolder.icon_short.setImageDrawable(AppBasicInfo.getIcon(MainApplication.getContext(), packageName, Constants.CONFIG_MCEREBRUM_DIR()));
        viewHolder.icon_long.setImageDrawable(AppBasicInfo.getIcon(MainApplication.getContext(), packageName, Constants.CONFIG_MCEREBRUM_DIR()));

        View.OnClickListener onClickListenerRun = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentAppSettings.LAUNCH);
            }
        };
        View.OnClickListener onClickListenerSettings = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentAppSettings.CONFIGURE);
            }
        };
        View.OnClickListener onClickListenerClear = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentAppSettings.CLEAR);
            }
        };

        boolean ismCerebrumSupported= AppAccess.getMCerebrumSupported(MainApplication.getContext(), packageName);
        boolean isConfigurable = AppAccess.getFuncConfigure(MainApplication.getContext(), packageName)!=null;
        boolean isConfigured = AppAccess.getConfigured(MainApplication.getContext(), packageName);
        boolean hasClear = AppAccess.getFuncClear(MainApplication.getContext(), packageName)!=null;
        boolean isEqualDefault = AppAccess.getConfigureMatch(MainApplication.getContext(), packageName);
        boolean isInstalled = AppInstall.getInstalled(MainApplication.getContext(), packageName);
        if(!ismCerebrumSupported || !isConfigurable)
            set(viewHolder.buttonSettingsLong, viewHolder.buttonSettingsShort, false, DefaultBootstrapBrand.SECONDARY, true, onClickListenerSettings);
        else if(isConfigured)
            set(viewHolder.buttonSettingsLong, viewHolder.buttonSettingsShort, true, DefaultBootstrapBrand.SUCCESS, true, onClickListenerSettings);
        else
            set(viewHolder.buttonSettingsLong, viewHolder.buttonSettingsShort, true, DefaultBootstrapBrand.SUCCESS, false, onClickListenerSettings);

        if(!ismCerebrumSupported || !hasClear)
            set(viewHolder.buttonClearLong, viewHolder.buttonClearShort, false, DefaultBootstrapBrand.SECONDARY, true, onClickListenerClear);
        else
            set(viewHolder.buttonClearLong, viewHolder.buttonClearShort, true, DefaultBootstrapBrand.DANGER, true, onClickListenerClear);
        if(!isInstalled || packageName.equals(AppBasicInfo.getMCerebrum(MainApplication.getContext())))
            set(viewHolder.buttonLaunchLong, viewHolder.buttonLaunchShort, false, DefaultBootstrapBrand.SECONDARY, true, onClickListenerRun);
        else
        set(viewHolder.buttonLaunchLong, viewHolder.buttonLaunchShort, true, DefaultBootstrapBrand.SUCCESS, true, onClickListenerRun);

        if(!isInstalled){
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            viewHolder.status.setText("not installed");
        }else if(ismCerebrumSupported  && isConfigurable && isConfigured && isEqualDefault){
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            viewHolder.status.setText("configured");
        }else if(ismCerebrumSupported && isConfigurable && isConfigured && !isEqualDefault &&AppBasicInfo.getUseAs(MainApplication.getContext(), packageName).equalsIgnoreCase(MCEREBRUM.APP.USE_AS_REQUIRED)){
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
            viewHolder.status.setText("partially configured");
        }
        else if(ismCerebrumSupported  && isConfigurable && AppBasicInfo.getUseAs(MainApplication.getContext(), packageName).equalsIgnoreCase(MCEREBRUM.APP.USE_AS_REQUIRED)){
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            viewHolder.status.setText("not configured");
        }else{
            viewHolder.status.setText("");
        }

        return cell;
    }

    private void set(BootstrapButton b1, BootstrapButton b2, boolean e, BootstrapBrand b, boolean o, View.OnClickListener l){
        b1.setEnabled(e);
        b1.setBootstrapBrand(b);
        b1.setShowOutline(o);
        b1.setOnClickListener(l);
        b2.setEnabled(e);
        b2.setBootstrapBrand(b);
        b2.setShowOutline(o);
        b2.setOnClickListener(l);
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
        BootstrapButton buttonSettingsLong;
        BootstrapButton buttonSettingsShort;
        BootstrapButton buttonLaunchLong;
        BootstrapButton buttonLaunchShort;
        BootstrapButton buttonClearLong;
        BootstrapButton buttonClearShort;
    }
}
