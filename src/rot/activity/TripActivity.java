/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import rot.model.RotStop;
import rot.model.RotTrip;
import rot.utils.TripAdapter;

/**
 *
 * @author user
 */
public class TripActivity extends Activity {

    private RotStop rotStop;
    private ListView trips;
    List<RotTrip> tripList;
    private final Handler handler = new Handler();
    private final Timer timer = new Timer();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.trip);

        final Intent intent = getIntent();
        rotStop = (RotStop) intent.getSerializableExtra("rotStop");
        
        timer.scheduleAtFixedRate(new ReloadActivity(), 0, 60000);
    }

    private void loadView() {
        final ProgressDialog dialog = ProgressDialog.show(TripActivity.this, "", "Chargement en cours. Veuillez patienter...", true);
        
        Thread worker = new Thread(new Runnable() {
            public void run() {
                try {
                    tripList = rotStop.getRotTrips().getTripList();
                } catch (Exception ex) {
                    Logger.getLogger(TripActivity.class.getName()).log(Level.SEVERE, null, ex);
                }
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            generateView();     
                            dialog.dismiss();
                        } catch (Exception ex) {
                            Logger.getLogger(TripActivity.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        });
        worker.start();        
    }
    
    private void generateView() throws IOException, JSONException {
        trips = (ListView) findViewById(R.id.trips);
        if (tripList.isEmpty()) {
            final Dialog noResultDialog = new Dialog(this);
            noResultDialog.setTitle("Information");
            noResultDialog.setCancelable(false);
            noResultDialog.setContentView(R.layout.noresultdialog);
            final TextView text = (TextView) noResultDialog.findViewById(R.id.dialogText);
            text.setText("Aucun passage à cet arrêt dans la prochaine heure.");
            final Button back = (Button) noResultDialog.findViewById(R.id.back);
            back.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
            noResultDialog.show();
        } else {
            final TextView locationName = (TextView) findViewById(R.id.locationName);
            locationName.setText(rotStop.getLocationName());
            
            final ListAdapter tripAdapter = new TripAdapter(this, tripList);
            trips.setAdapter(tripAdapter);
        }
        
        trips.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timer.cancel();
                final RotTrip rotTrip = (RotTrip) trips.getAdapter().getItem(position);
                final Intent intent = new Intent(getApplicationContext(), SetReminderActivity.class);
                intent.putExtra("rotStop", rotStop);
                intent.putExtra("rotTrip", rotTrip);
                startActivity(intent);
            }
        });
        
        final Button reload = (Button) findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                try {
                    loadView();
                } catch (Exception ex) {
                    Logger.getLogger(TripActivity.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });        

        final Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                onBackPressed();
            }
        });   
    }
    
    
    @Override
    public void onBackPressed() {
        timer.cancel();
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    
    class ReloadActivity extends TimerTask {

        @Override
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    try {
                        loadView();
                    } catch (Exception ex) {
                        Logger.getLogger(TripActivity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });        
        }

    }    
    
}

