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

    @org.codehaus.jackson.annotate.JsonCreator
    @JsonCreator
    public AddRequestBean(@org.codehaus.jackson.annotate.JsonProperty("repoName")  @JsonProperty("repoName")  String repoName
                        , @org.codehaus.jackson.annotate.JsonProperty("repoOwner") @JsonProperty("repoOwner") String repoOwner)
    {
        this.repoName  = repoName;
        this.repoOwner = repoOwner;
    }

    @Override
    @org.codehaus.jackson.annotate.JsonProperty("repoName")
    @JsonProperty("repoName")
    public String getRepoName() { return repoName; }

    @Override
    @org.codehaus.jackson.annotate.JsonProperty("repoOwner")
    @JsonProperty("repoOwner")
    public String getRepoOwner() { return repoOwner; }
}
