/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.api;

import com.fasterxml.jackson.annotation.*;

import uk.ac.cam.cl.git.interfaces.RepoUserRequestInterface;

/**
 * Concrete implementation of RepoUserRequestInterface, for internal
 * (deserialization) use.
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 */
public class RepoUserRequestBean implements RepoUserRequestInterface
{
    private final String repoName;
    private final String userName;

    @JsonCreator
    public RepoUserRequestBean(@JsonProperty("repoName")  String repoName
                             , @JsonProperty("userName") String userName)
    {
        this.repoName = repoName;
        this.userName = userName;
    }

    @Override
    @JsonProperty("repoName")
    public String getRepoName() { return repoName; }

    @Override
    @JsonProperty("userName")
    public String getUserName() { return userName; }
}
