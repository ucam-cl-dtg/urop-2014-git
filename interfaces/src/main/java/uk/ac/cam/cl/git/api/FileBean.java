/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.api;

import com.fasterxml.jackson.annotation.*;

/**
 * This is a container for a file, for when all the files are requested
 * with one call (to have less latency).
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 */

public class FileBean
{
    private final String name;
    private final String content;

    @JsonCreator
    public FileBean(@JsonProperty("name")    String name
                  , @JsonProperty("content") String content)
    {
        this.name    = name;
        this.content = content;
    }

    @JsonProperty("name")
    public String getName() { return name; }

    @JsonProperty("content")
    public String getContent() { return content; }
}
