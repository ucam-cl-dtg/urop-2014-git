/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import java.io.ByteArrayOutputStream;

/**
 * Encapsulates a process's stdout, stderr and return status.
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.1
 */
public class ProcessOutput
{
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private ByteArrayOutputStream err = new ByteArrayOutputStream();
    private int status = 0;

    public ProcessOutput ()
    {
        super();
    }

    public ByteArrayOutputStream getOut() { return out; }
    public ByteArrayOutputStream getErr() { return err; }
    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }
}
