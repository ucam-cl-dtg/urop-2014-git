/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git.interfaces;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is the interface for the object that we expect when a request
 * is made to add a new repository. The object can be in JSON, Jackson2
 * will convert it into this format.
 * 
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public interface AddRequestInterface {
    @JsonProperty("repoName")
    public String getRepoName();
    
    @JsonProperty("repoOwner")
    public String getRepoOwner();

}
