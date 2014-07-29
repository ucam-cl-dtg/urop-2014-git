/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.api;

import java.util.Date;

import com.fasterxml.jackson.annotation.*;

/**
 * Object to hold one commit from a versioned system (currently git).
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 */
public class Commit
{
    private final String author;
    private final String message;
    private final String commitID;
    private final Date   commitTime;

    @JsonCreator
    public Commit(@JsonProperty("commitId")   String commitID
                , @JsonProperty("author")     String author
                , @JsonProperty("message")    String message
                , @JsonProperty("commitTime") Date   commitTime)
    {
        this.commitID = commitID;
        this.author = author;
        this.message = message;
        this.commitTime = commitTime;
    }

    /**
     * @return the commitID
     */
    public String getCommitID()
    {
        return commitID;
    }

    /**
     * @return The author
     */
    @JsonProperty("author")
    public String getAuthor()
    {
        return author;
    }

    /**
     * @return The message
     */
    @JsonProperty("message")
    public String getMessage()
    {
        return message;
    }

    /**
     * @return The commitTime
     */
    @JsonProperty("commitTime")
    public Date getCommitTime()
    {
        return commitTime;
    }

}
