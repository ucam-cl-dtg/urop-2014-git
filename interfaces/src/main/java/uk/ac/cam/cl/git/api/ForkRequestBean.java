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
{
    private final String newRepoName;
    private final String userName;
    private final String repoName;

    @JsonCreator
    public ForkRequestBean(@JsonProperty("newRepoName") String newRepoName
                         , @JsonProperty("userName")    String userName
                         , @JsonProperty("repoName")    String repoName)
    {
        if (newRepoName == null)
            this.newRepoName = userName + "/" + repoName;
        else
            this.newRepoName  = newRepoName;
        this.userName = userName;
        this.repoName = repoName;
    }

    @Override
    @JsonProperty("newRepoName")
    public String getNewRepoName() { return newRepoName; }

    @Override
    @JsonProperty("userName")
    public String getUserName() { return userName; }

    @Override
    @JsonProperty("repoName")
    public String getRepoName() { return repoName; }
}
