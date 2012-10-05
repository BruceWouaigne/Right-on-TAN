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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import rot.model.RotStop;
import rot.model.RotTrip;

/**
 *
 * @author user
 */
public class SetReminderActivity extends Activity {

    private RotStop rotStop;
    private RotTrip rotTrip;
    private final Handler handler = new Handler();
    private final Timer timer = new Timer();
    
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.reminder);

        final Intent intent = getIntent();
        rotStop = (RotStop) intent.getSerializableExtra("rotStop");
        rotTrip = (RotTrip) intent.getSerializableExtra("rotTrip");

        timer.scheduleAtFixedRate(new SetReminderActivity.ReloadActivity(), 0, 60000);
        
    }
    
    private void loadView() throws IOException, JSONException {
        final ProgressDialog dialog = ProgressDialog.show(SetReminderActivity.this, "", "Chargement en cours. Veuillez patienter...", true);
        
        Thread worker = new Thread(new Runnable() {
            public void run() {
                try {
                    rotTrip = rotTrip.reloadTrip();
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

    private void generateView() {  
        if (rotTrip == null) {
            final Dialog noResultDialog = new Dialog(this);
            noResultDialog.setTitle("Information");
            noResultDialog.setCancelable(false);
            noResultDialog.setContentView(R.layout.noresultdialog);
            final TextView text = (TextView) noResultDialog.findViewById(R.id.dialogText);
            text.setText("Ce voyage n'est plus disponible.");
            final Button back = (Button) noResultDialog.findViewById(R.id.back);
            back.setOnClickListener(new View.OnClickListener() {
                
                public void onClick(View arg0) {
                    timer.cancel();
                    final Intent intent = new Intent(getApplicationContext(), TripActivity.class);
                    intent.putExtra("rotStop", rotStop);
                    startActivity(intent);
                }
            });
            noResultDialog.show();
        } else {
            final ImageView ligneNumber = (ImageView) findViewById(R.id.ligneNumber);
            int ressource = getApplicationContext().getResources().getIdentifier("ligne_" + rotTrip.getLigneNumber().toLowerCase(), "drawable", getApplicationContext().getPackageName());
            ligneNumber.setImageResource(ressource);

            final TextView locationName = (TextView) findViewById(R.id.locationName);
            locationName.setText(rotStop.getLocationName());

            final TextView terminus = (TextView) findViewById(R.id.terminus);
            terminus.setText(rotTrip.getTerminus());

            final TextView wait = (TextView) findViewById(R.id.wait);
            wait.setText(rotTrip.getWait());

        }
                
        final Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                timer.cancel();
                final Intent intent = new Intent(getApplicationContext(), TripActivity.class);
                intent.putExtra("rotStop", rotStop);
                startActivity(intent);
            }
        });      
        
        final Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final EditText remind = (EditText) findViewById(R.id.remind);
                if (remind.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Vous devez saisir l'heure de rappel.", Toast.LENGTH_SHORT).show();
                } else {
                    timer.cancel();
                    final Intent intent = new Intent(getApplicationContext(), RemindActivity.class);
                    intent.putExtra("rotStop", rotStop);
                    intent.putExtra("rotTrip", rotTrip);
                    intent.putExtra("remind", ((EditText) findViewById(R.id.remind)).getText().toString());
                    startActivity(intent);                      
                }
            }
        });        
    }
    
    @Override
    public void onBackPressed() {
        timer.cancel();
        final Intent intent = new Intent(getApplicationContext(), TripActivity.class);
        intent.putExtra("rotStop", rotStop);
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
