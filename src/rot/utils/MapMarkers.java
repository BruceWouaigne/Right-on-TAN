/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import java.util.List;
import rot.activity.R;
import rot.activity.TripActivity;
import rot.model.RotStop;
import rot.model.RotStops;

/**
 *
 * @author user
 */
public class MapMarkers extends ItemizedOverlay<OverlayItem> {

    private List<OverlayItem> overlayItemList;
    private RotStops rotStops;
    private Context context;
    private OverlayItem item;

    public MapMarkers(Drawable arg0, Context context, RotStops rotStop, List<OverlayItem> overlayItemList) {
        super(boundCenterBottom(arg0));
        this.context = context;
        this.rotStops = rotStop;
        this.overlayItemList = overlayItemList;
        populate();
    }

    public void addItem(GeoPoint point, String title, String snippet) {
        OverlayItem newItem = new OverlayItem(point, title, snippet);
        overlayItemList.add(newItem);
        populate();
    }

    @Override
    protected boolean onTap(int index) {
        item = overlayItemList.get(index);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Station sélectionnée : " + item.getTitle()).setCancelable(false);
        
        builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                final RotStop rotStop = rotStops.findByLocationCode(item.getSnippet());
                if (rotStop == null) {
                    final Dialog noResultDialog = new Dialog(context);
                    noResultDialog.setTitle("Information");
                    noResultDialog.setCancelable(false);
                    noResultDialog.setContentView(R.layout.noresultdialog);
                    final TextView text = (TextView) noResultDialog.findViewById(R.id.dialogText);
                    text.setText("Aucun passage à cet arrêt.");
                    final Button back = (Button) noResultDialog.findViewById(R.id.back);
                    back.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View arg0) {
                            noResultDialog.dismiss();
                        }
                    });
                    noResultDialog.show();            
                } else {
                    final Intent intent = new Intent(context, TripActivity.class);
                    intent.putExtra("rotStop", rotStop);
                    context.startActivity(intent); 
                }                
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();          
        
        
        

        return true;
    }    
    
    @Override
    protected OverlayItem createItem(int i) {
        return overlayItemList.get(i);
    }

    @Override
    public int size() {
        return overlayItemList.size();
    }
}
