/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.logging.Level;
import java.util.logging.Logger;
import rot.model.RotStop;
import rot.model.RotTrip;

/**
 *
 * @author user
 */
public class TimeToGoActivity extends Activity {

    private RotStop rotStop;
    private RotTrip rotTrip;
    private String remind;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.timetogo);

        final Intent intent = getIntent();
        rotStop = (RotStop) intent.getSerializableExtra("rotStop");
        rotTrip = (RotTrip) intent.getSerializableExtra("rotTrip");    
        remind = intent.getStringExtra("remind");
        
        try {
            final AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), null);
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            final long[] pattern = {0, 200, 500};
            vibrator.vibrate(pattern, 0);

            final Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, alert);

            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception ex) {
            Logger.getLogger(RemindActivity.class.getName()).log(Level.SEVERE, null, ex);
        }

        final ImageView ligneNumber = (ImageView) findViewById(R.id.ligneNumber);
        int ressource = getApplicationContext().getResources().getIdentifier("ligne_" + rotTrip.getLigneNumber().toLowerCase(), "drawable", getApplicationContext().getPackageName());
        ligneNumber.setImageResource(ressource);

        final TextView locationName = (TextView) findViewById(R.id.locationName);
        locationName.setText(rotStop.getLocationName());

        final TextView terminus = (TextView) findViewById(R.id.terminus);
        terminus.setText(rotTrip.getTerminus());  
        
        final TextView message = (TextView) findViewById(R.id.message);
        message.setText("Départ dans " + remind + " minute(s).");
        
        final Button cancel = (Button) findViewById(R.id.close);
        cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                mediaPlayer.stop();
                vibrator.cancel();
                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
    
    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        vibrator.cancel();
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }    

}
