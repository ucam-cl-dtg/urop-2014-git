/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import com.fasterxml.jackson.annotation.*;

import uk.ac.cam.cl.git.public_interfaces.ForkRequestInterface;

/**
 * Concrete implementation of ForkRequestInterface, for internal
 * (deserialization) use.
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 */
public class ForkRequestBean implements ForkRequestInterface
{
    private final String repoName;
    private final String repoOwner;
    private final String upstream;
    private final String overlay;

    @JsonCreator
    public ForkRequestBean(@JsonProperty("repoName")  String repoName
                         , @JsonProperty("repoOwner") String repoOwner
                         , @JsonProperty("upstream")  String upstream
                         , @JsonProperty("overlay")   String overlay)
    {
        this.repoName  = repoName;
        this.repoOwner = repoOwner;
        this.upstream  = upstream;
        this.overlay   = overlay;
    }

    @Override
    @JsonProperty("repoName")
    public String getRepoName() { return repoName; }

    @Override
    @JsonProperty("repoOwner")
    public String getRepoOwner() { return repoOwner; }
    
    @Override
    @JsonProperty("upstream")
    public String getUpstream() { return upstream; }

    @Override
    @JsonProperty("overlay")
    public String getOverlay() { return overlay; }
}
