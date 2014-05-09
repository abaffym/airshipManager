/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.airshipmanagergui;

/**
 *
 * @author Marek
 */
public class AirshipManagerException extends Exception {

    /**
     * Creates a new instance of <code>AirshipManagerException</code> without
     * detail message.
     */
    public AirshipManagerException() {
    }

    /**
     * Constructs an instance of <code>AirshipManagerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AirshipManagerException(String msg) {
        super(msg);
    }

    public AirshipManagerException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
