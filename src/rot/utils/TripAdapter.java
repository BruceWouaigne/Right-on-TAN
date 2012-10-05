/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.util.List;
import org.json.JSONException;
import rot.activity.R;
import rot.model.RotTrip;

/**
 *
 * @author user
 */
public class TripAdapter extends BaseAdapter {

    public List<RotTrip> tripList;
    LayoutInflater inflater;

    public TripAdapter(Context context, List<RotTrip> rotTripList) throws IOException, JSONException {
        this.tripList = rotTripList;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.tripList.size();
    }

    public Object getItem(int position) {
        return tripList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        
        convertView = inflater.inflate(R.layout.itemtrip, null);
        
        ImageView ligneNumber = (ImageView) convertView.findViewById(R.id.ligneNumber);
        int ressource = convertView.getContext().getResources().getIdentifier("ligne_" + tripList.get(position).getLigneNumber().toLowerCase(), "drawable", convertView.getContext().getPackageName());
        ligneNumber.setImageResource(ressource);
        
        TextView terminus = (TextView)convertView.findViewById(R.id.terminus);
        terminus.setText(tripList.get(position).getTerminus());

        TextView wait = (TextView)convertView.findViewById(R.id.wait);
        wait.setText(tripList.get(position).getWait());

        return convertView;
    }

}

