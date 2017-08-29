package org.md2k.mcerebrum.UI.folding_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.ramotion.foldingcell.FoldingCell;

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.Application;

import java.util.HashSet;
import java.util.List;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */
public class FoldingCellListAdapter extends ArrayAdapter<Application> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;


    public FoldingCellListAdapter(Context context, List<Application> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item for selected view
        Application app = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell, parent, false);
            viewHolder.title = (TextView) cell.findViewById(R.id.textview_title);
            viewHolder.summary = (TextView) cell.findViewById(R.id.textview_description);
            viewHolder.icon = (ImageView) cell.findViewById(R.id.imageview_icon);
            viewHolder.content_title = (TextView) cell.findViewById(R.id.textview_content_title);
            viewHolder.content_summary = (TextView) cell.findViewById(R.id.textview_content_description);
            viewHolder.description =(TextView)cell.findViewById(R.id.textview_content_detail);
            viewHolder.version=(TextView)cell.findViewById(R.id.textview_version_number);
            viewHolder.updateVersion =(TextView)cell.findViewById(R.id.textview_update_date);
            viewHolder.install=(BootstrapButton)cell.findViewById(R.id.button_install);
            viewHolder.update=(BootstrapButton)cell.findViewById(R.id.button_update);
            viewHolder.uninstall=(BootstrapButton)cell.findViewById(R.id.button_uninstall);

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
        viewHolder.title.setText(app.getTitle(getContext()));
        viewHolder.summary.setText(app.getSummary(getContext()));
        viewHolder.content_title.setText(app.getTitle(getContext()));
        viewHolder.content_summary.setText(app.getSummary(getContext()));
        viewHolder.description.setText(app.getDescription(getContext()));
        if(app.isInstalled()) {
            viewHolder.install.setEnabled(false);
            viewHolder.uninstall.setEnabled(true);
            viewHolder.update.setEnabled(false);
            viewHolder.version.setText(app.getVersionName());
//            viewHolder.icon.setImageDrawable(app.getIcon());
        }
        else {
            viewHolder.install.setEnabled(true);
            viewHolder.uninstall.setEnabled(false);
            viewHolder.update.setEnabled(false);
            viewHolder.version.setText("<not installed>");
        }
//        viewHolder.icon.setImageDrawable(app.getIcon(getContext());
//        viewHolder.version.setText(app.getVersionName());
        viewHolder.updateVersion.setText("N/A");

     //   viewHolder.requestsCount.setText(String.valueOf(item.getRequestsCount()));
   //     viewHolder.pledgePrice.setText(item.getPledgePrice());
        // set custom btn handler for list item from that item

  /*      if (app.getRequestBtnClickListener() != null) {
            viewHolder.contentRequestBtn.setOnClickListener(app.getRequestBtnClickListener());
        } else {
            // (optionally) add "default" handler if no handler found in item
            viewHolder.contentRequestBtn.setOnClickListener(defaultRequestBtnClickListener);
        }
*/

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

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView summary;
        ImageView icon;
        TextView content_title;
        TextView content_summary;
        TextView description;
        TextView version;
        TextView updateVersion;
        BootstrapButton install;
        BootstrapButton uninstall;
        BootstrapButton update;
    }
}
