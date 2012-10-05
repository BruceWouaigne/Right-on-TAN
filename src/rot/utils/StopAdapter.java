/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import rot.activity.R;
import rot.model.RotStop;

/**
 *
 * @author user
 */
public class StopAdapter extends BaseAdapter {

    public List<RotStop> stopListSafe;
    public List<RotStop> stopList;
    LayoutInflater inflater;

    public StopAdapter(Context context, List<RotStop> rotStopList) throws IOException, JSONException {
        this.stopList = rotStopList;
        this.stopListSafe = new ArrayList<RotStop>(stopList);
        Collections.copy(stopListSafe, stopList);
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.stopList.size();
    }

    public Object getItem(int position) {
        return stopList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.itemstop, null);

        final TextView locationName = (TextView) convertView.findViewById(R.id.locationName);
        locationName.setText(stopList.get(position).getLocationName());

        final TableLayout layout = (TableLayout) convertView.findViewById(R.id.layoutLigneLogos);
        TableRow row = new TableRow(convertView.getContext());
        
        for (int i = 0; i <= stopList.get(position).getLigneNumber().size() - 1; i++) {
            
            int ressource = convertView.getContext().getResources().getIdentifier("ligne_" + stopList.get(position).getLigneNumber().get(i).toLowerCase(), "drawable", convertView.getContext().getPackageName());
            ImageView img = new ImageView(convertView.getContext());

            img.setImageResource(ressource);
            img.setBackgroundColor(Color.TRANSPARENT);
            img.setAdjustViewBounds(true);
            img.setMaxWidth(40);
            img.setPadding(0, 5, 5, 5); 
            
            if (row.getVirtualChildCount() < 3) {
                row.addView(img);
            }
            
            if (row.getVirtualChildCount() == 3 || i == (stopList.get(position).getLigneNumber().size() - 1)) {
                layout.addView(row);
                row = new TableRow(convertView.getContext());
            }
            
        }

        return convertView;
    }

    public void filter(String searchValue) {

        stopList.clear();

        if (searchValue.equals("")) {
            stopList = new ArrayList<RotStop>(stopListSafe);
            Collections.copy(stopList, stopListSafe);
        } else {
            for (RotStop rotStop : stopListSafe) {
                if (rotStop.getLocationName().toLowerCase().contains(searchValue.toLowerCase())) {
                    stopList.add(rotStop);
                }
            }
        }

        this.notifyDataSetChanged();
    }
}
