/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import com.fasterxml.jackson.annotation.*;

import uk.ac.cam.cl.git.public_interfaces.AddRequestInterface;

/**
 * Concrete implementation of ForkRequestInterface, for internal
 * (deserialization) use.
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 */
public class AddRequestBean implements AddRequestInterface
{
    private final String repoName;
    private final String repoOwner;

    @JsonCreator
    public AddRequestBean(@JsonProperty("repoName")  String repoName
                        , @JsonProperty("repoOwner") String repoOwner)
    {
        this.repoName  = repoName;
        this.repoOwner = repoOwner;
    }

    @Override
    @JsonProperty("repoName")
    public String getRepoName() { return repoName; }

    @Override
    @JsonProperty("repoOwner")
    public String getRepoOwner() { return repoOwner; }
}
