/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.maps.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rot.model.RotStops;
import rot.utils.MapMarkers;

/**
 *
 * @author user
 */
public class StopMapActivity extends MapActivity {

    private RotStops rotStops;
    private MapController mapController;
    private MapMarkers markers;
    private List<OverlayItem> overlayItemList;
    private final Handler handler = new Handler();
    private ProgressDialog dialog;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.stopmap);

        final Intent intent = getIntent();
        rotStops = (RotStops) intent.getSerializableExtra("rotStops");

        loadView();
    }

    private void loadView() {
        dialog = ProgressDialog.show(StopMapActivity.this, "", "Chargement en cours. Veuillez patienter...", true);

        Thread worker = new Thread(new Runnable() {

            public void run() {
                try {
                    overlayItemList = new ArrayList<OverlayItem>();
                    String[] stopLocations = getResources().getStringArray(R.array.stops);
                    for (String stopLocation : stopLocations) {
                        final String[] result = stopLocation.split("\\|");
                        final GeoPoint point = new GeoPoint((int) (Double.parseDouble(result[2]) * 1E6), (int) (Double.parseDouble(result[3]) * 1E6));
                        overlayItemList.add(new OverlayItem(point, result[1], result[0]));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(StopMapActivity.class.getName()).log(Level.SEVERE, null, ex);
                }
                handler.post(new Runnable() {

                    public void run() {
                        try {
                            generateView();
                        } catch (Exception ex) {
                            Logger.getLogger(StopMapActivity.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        });
        worker.start();
    }

    public void generateView() {
        MapView mapView = (MapView) findViewById(R.id.stopMap);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(17);

        markers = new MapMarkers(getResources().getDrawable(R.drawable.logo_tan_stop), this, rotStops, overlayItemList);
        mapView.getOverlays().add(markers);

        Criteria criteria = new Criteria();
        criteria.setCostAllowed(false);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(criteria, new LocationListener() {

            public void onLocationChanged(Location location) {
                GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
                mapController.animateTo(point);
                dialog.dismiss();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        }, null);        
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
