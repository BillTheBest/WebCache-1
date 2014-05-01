import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* Credit: this code is inspired from 
 * http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html
 */
public class ThreadPoolManager implements Runnable {
  protected static ServerSocket serverSocket;
  protected boolean isStopped = false;
  protected Thread runningThread = null;
  protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

  public ThreadPoolManager (ServerSocket s) {
    this.serverSocket = s;
  }
  
  public void run() {
    synchronized(this) {
      this.runningThread = Thread.currentThread();
    }
    
    /** Main loop. Listen for incoming connections and spawn a new
     * thread for handling them */
    Socket client = null;
    
    while (! isStopped()) {
      try {
        /* ServerSocket.accept() returns a new socket when a connection is 
         * made. The method will block, but when it returns the socket will
         * be connected to the client.
         * Also, spawn a new thread for each request.
         * */
        client = serverSocket.accept();
        this.threadPool.execute(new WorkerRunnable(client));
//        handle(client); /* The main work */
      } catch (IOException e) {
        if(isStopped()) {
          System.out.println("Server Stopped.") ;
          return;
        }
        System.out.println("Error reading request from client: " + e);
        /* Definitely cannot continue processing this request,
         * so skip to next iteration of while loop. */
        continue;
      }
    }
    this.threadPool.shutdown();
    System.out.println("Server Stopped.") ;
  }
  
  public static void handle(Socket client) {
    Socket server = null;
    HttpRequest request = null;
    HttpResponse response = null;
  
    /* Process request. If there are any exceptions, then simply
     * return and end this request. This unfortunately means the
     * client will hang for a while, until it timeouts. */
  
    /* Read request */
    try {
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        request = new HttpRequest(fromClient);
    } catch (IOException e) {
        System.out.println("Error reading request from client: " + e);
        return;
    }
    /* Send request to server */
    try {
        /* Open socket and write request to socket */
        server = serverSocket.accept();
        DataOutputStream toServer = new DataOutputStream(client.getOutputStream()); // Should it be client.getOutputStream() instead?
        /* Fill in. KM TODO: start sending our HTTP request  */
    } catch (UnknownHostException e) {
        System.out.println("Unknown host: " + request.getHost());
        System.out.println(e);
        return;
    } catch (IOException e) {
        System.out.println("Error writing request to server: " + e);
        return;
    }
    /* Read response and forward it to client */
    try {
        DataInputStream fromServer = new DataInputStream(server.getInputStream());
        response = new HttpResponse(fromServer);
        DataOutputStream toClient = new DataOutputStream(client.getOutputStream()); // Should it be server.getOutputStream() instead?
        /* Fill in */
        /* Write response to client. First headers, then body */
        client.close();
        server.close();
        /* Insert object into the cache */
        /* Fill in (optional exercise only) */
    } catch (IOException e) {
        System.out.println("Error writing response to client: " + e);
    }
  }
  
  private synchronized boolean isStopped() {
    return this.isStopped;
  }
  
  public synchronized void stop(){
    this.isStopped = true;
    try {
        this.serverSocket.close();
    } catch (IOException e) {
        throw new RuntimeException("Error closing server", e);
    }
  }

}
