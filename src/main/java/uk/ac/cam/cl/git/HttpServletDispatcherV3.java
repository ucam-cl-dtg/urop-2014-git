/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/rest/*" }, 
initParams = { 
		@WebInitParam(name = "javax.ws.rs.Application", value = "uk.ac.cam.cl.dtg.teaching.ApplicationRegister"),
		@WebInitParam(name = "resteasy.servlet.mapping.prefix", value="/api/")
})
public class HttpServletDispatcherV3 extends HttpServletDispatcher {
}
