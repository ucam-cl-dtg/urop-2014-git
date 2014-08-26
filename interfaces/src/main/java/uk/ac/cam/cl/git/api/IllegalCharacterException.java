/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */

package uk.ac.cam.cl.git.api;

/**
 * @author rmk35
 */
public class IllegalCharacterException extends Exception {

    private static final long serialVersionUID = 3383825633109578710L; /* Generated */

    public IllegalCharacterException(String message) {
        super(message);
    }

    public IllegalCharacterException() {
        super();
    }

}
