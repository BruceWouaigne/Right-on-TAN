/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 *
 * @author user
 */
public class AboutActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.about);
    }
    
    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }    
}
