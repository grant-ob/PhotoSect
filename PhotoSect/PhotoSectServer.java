import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.BoundedThreadPool;
import org.mortbay.log.Log;

public class PhotoSectServer {
    public static void main(String[] args) {
	try {
	    Server server = new Server(4004);
	    // set up a thread pool
	    BoundedThreadPool threadPool = new BoundedThreadPool();
	    threadPool.setMaxThreads( 100 );
	    server.setThreadPool(threadPool);

	    WebAppContext wac = new WebAppContext();
	    wac.setContextPath("/");
	    wac.setWar("war");

	    server.addHandler( wac );

	    server.setStopAtShutdown(true);
	    server.setSendServerVersion(true);
	    
	    server.start();
	    server.join();
	}
	catch( Exception ex ) {
	    Log.warn( ex );
	}
    }
}