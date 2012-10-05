package rot.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import rot.model.RotStop;
import rot.model.RotStops;
import rot.utils.StopAdapter;

public class MainActivity extends Activity {

    private ListView stops;
    private RotStops rotStops;
    private final Handler handler = new Handler();
    private EditText search;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        stops = (ListView) findViewById(R.id.stops);
        search = (EditText) findViewById(R.id.search);

        loadView();

    }

    private void loadView() {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "Chargement en cours. Veuillez patienter...", true);

        Thread worker = new Thread(new Runnable() {

            public void run() {
                try {
                    rotStops = new RotStops();
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
        try {
            final ListAdapter stopAdapter = new StopAdapter(getApplicationContext(), rotStops.getStopList());
            stops.setAdapter(stopAdapter);
        } catch (Exception ex) {
            Logger.getLogger(TripActivity.class.getName()).log(Level.SEVERE, null, ex);
        }

        search.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final StopAdapter adapter = (StopAdapter) stops.getAdapter();
                adapter.filter(search.getText().toString());
            }

            public void afterTextChanged(Editable s) {
            }
        });

        stops.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final RotStop rotStop = (RotStop) stops.getAdapter().getItem(position);
                final Intent intent = new Intent(getApplicationContext(), TripActivity.class);
                intent.putExtra("rotStop", rotStop);
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

        final Button location = (Button) findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                final Intent intent = new Intent(getApplicationContext(), StopMapActivity.class);
                intent.putExtra("rotStops", rotStops);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                final Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);                
                return true;
        }
        return false;
    }
}
