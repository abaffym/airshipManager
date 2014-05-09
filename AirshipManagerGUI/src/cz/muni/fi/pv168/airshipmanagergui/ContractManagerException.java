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
public class ContractManagerException extends Exception {

    /**
     * Creates a new instance of <code>ContractManagerException</code> without
     * detail message.
     */
    public ContractManagerException() {
    }

    /**
     * Constructs an instance of <code>ContractManagerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ContractManagerException(String msg) {
        super(msg);
    }

    public ContractManagerException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
