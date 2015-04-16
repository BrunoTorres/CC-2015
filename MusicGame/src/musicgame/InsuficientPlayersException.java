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
public class InsuficientPlayersException extends Exception {

    /**
     * Creates a new instance of <code>UserInexistenteException</code> without
     * detail message.
     */
    public InsuficientPlayersException() {
        super();
    }

    /**
     * Constructs an instance of <code>UserInexistenteException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InsuficientPlayersException(String msg) {
        super(msg);
    }
}
