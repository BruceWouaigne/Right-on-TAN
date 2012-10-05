/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import rot.model.RotStop;
import rot.model.RotTrip;

/**
 *
 * @author user
 */
public class TanDataManager {
    
    private static TanDataManager instance;

    private TanDataManager() {
    }

    public static TanDataManager getInstance() {
        if (instance == null) {
            instance = new TanDataManager();
        }
        return instance;
    }
    
    public List<RotStop> getRotStopList() throws IOException, JSONException {

        List<RotStop> rotStopList = new ArrayList<RotStop>();

        HttpGet request = new HttpGet("https://open.tan.fr/ewp/arrets.json");
        HttpClient client = new DefaultHttpClient();

        HttpResponse response = client.execute(request);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        String json = reader.readLine();
        JSONTokener tokener = new JSONTokener(json);
        JSONArray finalResult = new JSONArray(tokener);

        for (int i = 0; i < finalResult.length(); i++) {
            JSONObject json_data = finalResult.getJSONObject(i);
            RotStop rotStop = new RotStop();
            rotStop.setLocationCode(json_data.getString("codeLieu"));
            rotStop.setLocationName(json_data.getString("libelle"));
            rotStop.setDistance(json_data.getString("distance"));
            
            JSONArray json_lignes = json_data.getJSONArray("ligne");
            
            List<String> lignes = new ArrayList<String>();
            for (int y = 0; y < json_lignes.length(); y++) {
                JSONObject json_ligne = json_lignes.getJSONObject(y);
                lignes.add(json_ligne.getString("numLigne"));
            }
            rotStop.setLigneNumber(lignes);
            
            rotStopList.add(rotStop);
        }
        
        return rotStopList;
    }
    
    public List<RotTrip> getRotTripList(String locationCode) throws Exception {

        if (locationCode == null || locationCode.equals("")) {
            throw new Exception("location code must be valorised");
        }
        
        List<RotTrip> rotTripList = new ArrayList<RotTrip>();
        
        HttpGet request = new HttpGet("https://open.tan.fr/ewp/tempsattente.json/" + locationCode);
        HttpClient client = new DefaultHttpClient();

        HttpResponse response = client.execute(request);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        String json = reader.readLine();
        JSONTokener tokener = new JSONTokener(json);
        JSONArray finalResult = new JSONArray(tokener);

        for (int i = 0; i < finalResult.length(); i++) {
            JSONObject json_data = finalResult.getJSONObject(i);
            RotTrip rotTrip = new RotTrip();
            rotTrip.setDirection(json_data.getString("sens"));
            rotTrip.setTerminus(json_data.getString("terminus"));
            rotTrip.setTraficInformation(json_data.getBoolean("infotrafic"));
            rotTrip.setWait(json_data.getString("temps"));
            
            JSONObject json_ligne = json_data.getJSONObject("ligne");
            rotTrip.setLigneNumber(json_ligne.getString("numLigne"));
            rotTrip.setLigneType(json_ligne.getString("typeLigne"));
            rotTrip.setLocationCode(locationCode);
            rotTripList.add(rotTrip);
        }
        
        return rotTripList;
    }    
    
    public RotTrip reloadTrip(RotTrip rotTrip) throws Exception {
        List<RotTrip> tripList = this.getRotTripList(rotTrip.getLocationCode());
        List<RotTrip> sameTripList = new ArrayList<RotTrip>();
        
        
        for (RotTrip trip : tripList) {
            if (trip.getLigneNumber().equals(rotTrip.getLigneNumber()) && trip.getDirection().equals(rotTrip.getDirection())) {
                if (trip.getWait().equals(rotTrip.getWait())) {
                    return rotTrip;
                }
                sameTripList.add(trip);
            }
        }
        
        for (RotTrip trip : sameTripList) {
            if (parseWait(trip.getWait()) == parseWait(rotTrip.getWait()) -1 ) {
                return trip;
            }
        }
        
        return null;
    }

    private long parseWait(String wait) {
        return Long.valueOf(wait.replaceAll(" ", "").replaceAll("mn", ""));
    }
    
}
