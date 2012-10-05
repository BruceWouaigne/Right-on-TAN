/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.model;

import java.io.Serializable;
import java.util.List;
import rot.utils.TanDataManager;

/**
 *
 * @author user
 */
public class RotTrips implements Serializable {
    
    private List<RotTrip> tripList;

    public RotTrips(String locationName) throws Exception {
        this.tripList = TanDataManager.getInstance().getRotTripList(locationName);
    }

    public List<RotTrip> getTripList() {
        return tripList;
    }

    public void setTripList(List<RotTrip> tripList) {
        this.tripList = tripList;
    }
       
}
