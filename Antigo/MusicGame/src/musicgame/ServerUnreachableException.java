/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicgame;

/**
 *
 * @author patricia
 */
public class ServerUnreachableException extends Exception {

    /**
     * Creates a new instance of <code>ServerUnreachableException</code> without
     * detail message.
     */
    public ServerUnreachableException() {
    }

    /**
     * Constructs an instance of <code>ServerUnreachableException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ServerUnreachableException(String msg) {
        super(msg);
    }
}
