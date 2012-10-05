/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rot.model;

import rot.utils.TanDataManager;
import java.io.IOException;
import java.io.Serializable;
import org.json.JSONException;

/**
 *
 * @author user
 */
public class RotTrip implements Serializable {
    
    private String direction;
    private String terminus;
    private boolean traficInformation;
    private String wait;
    private String ligneNumber;
    private String ligneType;
    private String locationCode;

    @Override
    public String toString() {
        return "Ligne " + this.ligneNumber + " vers " + this.terminus + " ==> " + this.wait;
    }
    
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getLigneNumber() {
        return ligneNumber;
    }

    public void setLigneNumber(String ligneNumber) {
        this.ligneNumber = ligneNumber;
    }

    public String getLigneType() {
        return ligneType;
    }

    public void setLigneType(String ligneType) {
        this.ligneType = ligneType;
    }

    public String getTerminus() {
        return terminus;
    }

    public void setTerminus(String terminus) {
        this.terminus = terminus;
    }

    public boolean isTraficInformation() {
        return traficInformation;
    }

    public void setTraficInformation(boolean traficInformation) {
        this.traficInformation = traficInformation;
    }

    public String getWait() {
        return wait;
    }

    public void setWait(String wait) {
        this.wait = wait;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }
 
    public RotTrip reloadTrip() throws Exception {
        return TanDataManager.getInstance().reloadTrip(this);
    }
    
    public Integer getWaitNumber() {
        return Integer.valueOf(this.wait.replaceAll(" ", "").replaceAll("mn", ""));
    }
}
