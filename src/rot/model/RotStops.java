/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import org.json.JSONException;
import rot.utils.TanDataManager;

/**
 *
 * @author user
 */
public class RotStops implements Serializable {
    
    private List<RotStop> stopList;

    public RotStops() throws IOException, JSONException {
        this.stopList = TanDataManager.getInstance().getRotStopList();
    }
    
    public List<RotStop> getStopList() {
        return stopList;
    }

    public void setStopList(List<RotStop> stopList) {
        this.stopList = stopList;
    }

    public RotStop findByLocationCode(String locationCode) {
        for (RotStop rotStop : stopList) {
            if (rotStop.getLocationCode().equals(locationCode)) {
                return rotStop;
            }
        }
        return null;
    }
    
}
