/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.api;

import com.fasterxml.jackson.annotation.*;

import uk.ac.cam.cl.git.interfaces.ForkRequestInterface;

/**
 * Concrete implementation of ForkRequestInterface, for internal
 * (deserialization) use.
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 */
public class ForkRequestBean implements ForkRequestInterface
{ /* TODO: Figure out what is causing import of Jackson 1, only use
   * Jackson 1 or Jackson 2 but not both!
   */
    private final String newRepoName;
    private final String repoOwner;
    private final String repoName;
    private final String overlay;

    @JsonCreator
    public ForkRequestBean(@JsonProperty("newRepoName") String newRepoName
                         , @JsonProperty("repoOwner")   String repoOwner
                         , @JsonProperty("repoName")    String repoName
                         , @JsonProperty("overlay")     String overlay)
    {
        if (newRepoName == null)
            this.newRepoName = repoOwner + "/" + repoName;
        else
            this.newRepoName  = newRepoName;
        this.repoOwner = repoOwner;
        this.repoName  = repoName;
        this.overlay   = overlay;
    }

    @Override
    @JsonProperty("newRepoName")
    public String getNewRepoName() { return newRepoName; }

    @Override
    @JsonProperty("repoOwner")
    public String getRepoOwner() { return repoOwner; }
    
    @Override
    @JsonProperty("repoName")
    public String getRepoName() { return repoName; }

    @Override
    @JsonProperty("overlay")
    public String getOverlay() { return overlay; }
}
