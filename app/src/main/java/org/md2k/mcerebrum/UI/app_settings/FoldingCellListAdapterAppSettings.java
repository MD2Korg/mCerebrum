package org.md2k.mcerebrum.UI.app_settings;

import android.content.Context;
import android.util.Log;
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

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.AppInfo;
import org.md2k.mcerebrum.app.AppMC;

import java.util.HashSet;
import java.util.List;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */
public class FoldingCellListAdapterAppSettings extends ArrayAdapter<AppInfo> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;
    private ResponseCallBack responseCallBack;


    public FoldingCellListAdapterAppSettings(Context context, List<AppInfo> apps, ResponseCallBack responseCallBack) {
        super(context, 0, apps);
        this.responseCallBack = responseCallBack;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // get item for selected view
        final AppInfo appInfo = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
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
            viewHolder.buttonLaunchLong = (BootstrapButton) cell.findViewById(R.id.button_launch_long);
            viewHolder.buttonSettingsShort = (BootstrapButton) cell.findViewById(R.id.button_settings_short);
            viewHolder.buttonLaunchShort = (BootstrapButton) cell.findViewById(R.id.button_launch_short);
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
        viewHolder.title.setText(appInfo.getTitle());
        viewHolder.summary.setText(appInfo.getSummary());
        viewHolder.content_title.setText(appInfo.getTitle());
        viewHolder.content_summary.setText(appInfo.getSummary());
        viewHolder.description.setText(appInfo.getDescription());
        String versionName = appInfo.getCurrentVersionName();
        if (versionName == null) versionName = "N/A";
        viewHolder.version.setText(versionName);
        viewHolder.icon_short.setImageDrawable(appInfo.getIcon(getContext()));
        viewHolder.icon_long.setImageDrawable(appInfo.getIcon(getContext()));

        View.OnClickListener onClickListenerRun = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentFoldingUIAppSettings.LAUNCH);
            }
        };
        View.OnClickListener onClickListenerSettings = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentFoldingUIAppSettings.CONFIGURE);
            }
        };

        if(!appInfo.isMCerebrumSupported() || appInfo.getInfo()==null || !appInfo.getInfo().isConfigurable())
            set(viewHolder.buttonSettingsLong, viewHolder.buttonSettingsShort, false, DefaultBootstrapBrand.SECONDARY, true, onClickListenerSettings);
        else if(appInfo.getInfo().isConfigured())
            set(viewHolder.buttonSettingsLong, viewHolder.buttonSettingsShort, true, DefaultBootstrapBrand.SUCCESS, true, onClickListenerSettings);
        else
            set(viewHolder.buttonSettingsLong, viewHolder.buttonSettingsShort, true, DefaultBootstrapBrand.SUCCESS, false, onClickListenerSettings);

        set(viewHolder.buttonLaunchLong, viewHolder.buttonLaunchShort, true, DefaultBootstrapBrand.SUCCESS, true, onClickListenerRun);
        viewHolder.buttonLaunchLong.setFontAwesomeIcon("fa_play");
        viewHolder.buttonLaunchShort.setFontAwesomeIcon("fa_play");
        Log.d("abc",appInfo.getTitle());
        if(appInfo.isMCerebrumSupported() && appInfo.getInfo()!=null && appInfo.getInfo().isConfigurable() && appInfo.getInfo().isConfigured() && appInfo.getInfo().isEqualDefault()){
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            viewHolder.status.setText("configured");
        }else if(appInfo.isMCerebrumSupported() && appInfo.getInfo()!=null && appInfo.getInfo().isConfigurable()){
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            viewHolder.status.setText("not configured");
        }else{
            viewHolder.status.setText("");
        }
        viewHolder.updateVersion.setText("N/A");

        //   viewHolder.requestsCount.setText(String.valueOf(item.getRequestsCount()));
        //     viewHolder.pledgePrice.setText(item.getPledgePrice());
        // set custom btn handler for list item from that item

  /*      if (appInfo.getRequestBtnClickListener() != null) {
            viewHolder.contentRequestBtn.setOnClickListener(appInfo.getRequestBtnClickListener());
        } else {
            // (optionally) add "default" handler if no handler found in item
            viewHolder.contentRequestBtn.setOnClickListener(defaultRequestBtnClickListener);
        }
*/

        return cell;
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
        BootstrapButton buttonLaunchLong;
        BootstrapButton buttonSettingsShort;
        BootstrapButton buttonLaunchShort;
    }
}
