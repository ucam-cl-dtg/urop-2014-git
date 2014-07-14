/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.public_interfaces;

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
    @JsonProperty("repoName")
    public String getRepoName();

    @JsonProperty("repoOwner")
    public String getRepoOwner();

    @JsonProperty("upstream")
    public String getUpstream();

    @JsonProperty("overlay")
    public String getOverlay();
}
