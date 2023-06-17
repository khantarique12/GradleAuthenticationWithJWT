package GradleAuthenticationWithJWT;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyServer {
	public static void main(String[] args) throws Exception {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		Server jettyServer = new Server(8080);
		jettyServer.setHandler(context);

		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);

		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", AuthResource.class.getCanonicalName());

		try {
			jettyServer.start();
			jettyServer.join();
		} finally {
	        jettyServer.stop();
	        jettyServer.destroy();
		}
	}
}








//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//
//public class JettyServer {
//  public static void main(String[] args) throws Exception {
//      int port = 8080;
//      
//      Server server = new Server(port);
//      ServletContextHandler servletContextHandler = new ServletContextHandler();
//      servletContextHandler.setContextPath("/");
//      servletContextHandler.addServlet(AuthResource.class, "/auth/*");
//
//      server.setHandler(servletContextHandler);
//      server.start();
//      server.join();
//  }
//}


//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//import org.eclipse.jetty.servlet.ServletHolder;
//import org.glassfish.jersey.servlet.ServletContainer;
//
//public class JettyServer {
//  public static void main(String[] args) throws Exception {
//      int port = 8080;
//      Server server = new Server(port);
//
//      ServletContextHandler servletContextHandler = new ServletContextHandler();
//      servletContextHandler.setContextPath("/");
//      server.setHandler(servletContextHandler);
//
//      ServletHolder authServlet = servletContextHandler.addServlet(ServletContainer.class, "/auth/*");
//      authServlet.setInitOrder(1);
//      authServlet.setInitParameter("jersey.config.server.provider.packages", "GradleAuthenticationWithJWT");
//
//      server.start();
//      server.join();
//  }
//}