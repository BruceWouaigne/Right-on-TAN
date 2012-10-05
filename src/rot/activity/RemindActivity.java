/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.activity;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
public class RemindActivity extends Activity {

    private RotStop rotStop;
    private RotTrip rotTrip;
    private String remind;
    private final Handler handler = new Handler();
    private final Timer timer = new Timer();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.remind);

        final Intent intent = getIntent();
        rotStop = (RotStop) intent.getSerializableExtra("rotStop");
        rotTrip = (RotTrip) intent.getSerializableExtra("rotTrip");
        remind = intent.getStringExtra("remind");

        timer.scheduleAtFixedRate(new RemindActivity.ReloadActivity(), 0, 60000);

    }

    private void loadView() throws IOException, JSONException {
        final ProgressDialog dialog = ProgressDialog.show(RemindActivity.this, "", "Chargement en cours. Veuillez patienter...", true);

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
            if (isTimeToGo()) {
                timer.cancel();
                final Intent intent = new Intent(getApplicationContext(), TimeToGoActivity.class);
                intent.putExtra("rotStop", rotStop);
                intent.putExtra("rotTrip", rotTrip);
                intent.putExtra("remind", remind);
                final PendingIntent pendingIntent = PendingIntent.getActivity(this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                final AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pendingIntent);
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

                final TextView remindText = (TextView) findViewById(R.id.remind);
                remindText.setText(remind);
            }

        }

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

        final Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                cancelDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        cancelDialog();
    }

    private void cancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Vous êtes sur le point de supprimer le rappel.").setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                timer.cancel();
                final Intent intent = new Intent(getApplicationContext(), TripActivity.class);
                intent.putExtra("rotStop", rotStop);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();        
    }
    
    private boolean isTimeToGo() {
        Calendar date = new GregorianCalendar();
        date.add(Calendar.MINUTE, rotTrip.getWaitNumber() - Integer.valueOf(remind));
        if (date.compareTo(new GregorianCalendar()) <= 0) {
            return true;
        } else {
            return false;
        }
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
