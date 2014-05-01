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
  protected ServerSocket serverSocket;
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
    
    while (!isStopped()) {
      try {
        /* ServerSocket.accept() returns a new socket when a connection is 
         * made. The method will block, but when it returns the socket will
         * be connected to the client.
         * Also, spawn a new worker thread for each request.
         * */
        client = serverSocket.accept();
        this.threadPool.execute(new WorkerRunnable(client, serverSocket));
//        this.threadPool.execute(new WorkerRunnable(serverSocket.accept(), serverSocket));
        
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
