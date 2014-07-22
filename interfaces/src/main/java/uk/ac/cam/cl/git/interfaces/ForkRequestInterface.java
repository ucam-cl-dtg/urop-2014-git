/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.interfaces;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is the interface for the object that we expect for a fork
 * request. The object can be in JSON, Jackson2 will convert it into
 * this format.
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 */
public interface ForkRequestInterface
{
    /**
     * @return The name the owner would like to call the forked repository.
     */
    @JsonProperty("repoName")
    public String getNewRepoName();

    /**
     * @return The CRSID of the user making the fork request.
     */
    @JsonProperty("userName")
    public String getUserName();

    /**
     * @return The name of the repository to be forked.
     */
    @JsonProperty("upstream")
    public String getRepoName();

    /**
     * @return The name of the repository to be overlaid on submission.
     */
    @JsonProperty("overlay")
    public String getOverlay();
}
