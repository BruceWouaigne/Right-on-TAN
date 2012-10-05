/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import org.json.JSONException;

/**
 *
 * @author decharri
 */
public class RotStop implements Serializable {
    
    private String locationCode;
    private String locationName;
    private String distance;
    private List<String> ligneNumber;
    private RotTrips rotTrips;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public List<String> getLigneNumber() {
        return ligneNumber;
    }

    public void setLigneNumber(List<String> ligneNumber) {
        this.ligneNumber = ligneNumber;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public RotTrips getRotTrips() throws Exception {
        this.rotTrips = new RotTrips(this.locationCode);
        return rotTrips;
    }

    public void setRotTrips(RotTrips rotTrips) {
        this.rotTrips = rotTrips;
    }
    
}
