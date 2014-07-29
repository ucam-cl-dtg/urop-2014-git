/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */

package uk.ac.cam.cl.git.api;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class DuplicateRepoNameException extends Exception {

    private static final long serialVersionUID = -8435627648504161495L; /* Generated */

    public DuplicateRepoNameException(String url) {
        super(url);
    }
}
