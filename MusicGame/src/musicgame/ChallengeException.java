/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicgame;

public class ChallengeException extends Exception {

    /**
     * Creates a new instance of <code>ChallengeException</code> without detail
     * message.
     */
    public ChallengeException() {
    }

    /**
     * Constructs an instance of <code>ChallengeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ChallengeException(String msg) {
        super(msg);
    }
}
