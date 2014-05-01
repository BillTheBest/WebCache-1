import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/* Credit: this code is inspired from 
 * http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html
 */
public class WorkerRunnable implements Runnable {

    protected static Socket clientSocket = null;
    protected static ServerSocket serverSocket = null;

    public WorkerRunnable(Socket clientSocket, ServerSocket serverSocket) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
    }

    public void run() {
      try {
        Socket server = null;
        HttpRequest request = null;
        HttpResponse response = null;

        InputStream streamFromClient = clientSocket.getInputStream();
        OutputStream streamToClient = clientSocket.getOutputStream();
        InputStream streamFromServer = null;
        OutputStream streamToServer = null;
        long time = System.currentTimeMillis();
        
        /* Process request. If there are any exceptions, then simply
         * return and end this request. This unfortunately means the
         * client will hang for a while, until it timeouts. */

        /* Read request */
        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(streamFromClient));
            request = new HttpRequest(fromClient);
//            System.out.println("============== " + request.toString());
        } catch (Exception e) {
            System.out.println("Error reading request from client: " + e);
            return;
        }
        
        /* Send request to server */
        try {
            /* Open socket and write request to socket */
            server = serverSocket.accept();
            streamFromServer = server.getInputStream();
            streamToServer = server.getOutputStream();
            DataOutputStream toServer = new DataOutputStream(streamToServer);
            
            int bytesRead;
            toServer.writeBytes(request.toString());
            toServer.flush();
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
            DataInputStream fromServer = new DataInputStream(streamFromServer);
            response = new HttpResponse(fromServer);
            DataOutputStream toClient = new DataOutputStream(streamToClient); // Should it be server.getOutputStream() instead?
            /* Fill in */
            /* Write response to client. First headers, then body */
            clientSocket.close();
            server.close();
            /* Insert object into the cache */
            /* Fill in (optional exercise only) */
        } catch (IOException e) {
            System.out.println("Error writing response to client: " + e);
        }
        
//        output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
//                this.serverText + " - " + time + "").getBytes());
        
        streamToClient.close();
        streamFromClient.close();
        
        System.out.println("Request processed: " + time);
        
      } catch (IOException e) {
        //report exception somewhere.
        e.printStackTrace();
      }
    }
    
    
    
    public static void handle() {
      Socket server = null;
      HttpRequest request = null;
      HttpResponse response = null;
    
      /* Process request. If there are any exceptions, then simply
       * return and end this request. This unfortunately means the
       * client will hang for a while, until it timeouts. */
    
      /* Read request */
      try {
          BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          request = new HttpRequest(fromClient);
      } catch (IOException e) {
          System.out.println("Error reading request from client: " + e);
          return;
      }
      
      /* ====================== I completed everything above this line ================== */
      
      /* Send request to server */
      try {
          /* Open socket and write request to socket */
          server = serverSocket.accept();
          DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream()); // Should it be client.getOutputStream() instead?
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
          DataOutputStream toClient = new DataOutputStream(clientSocket.getOutputStream()); // Should it be server.getOutputStream() instead?
          /* Fill in */
          /* Write response to client. First headers, then body */
          clientSocket.close();
          server.close();
          /* Insert object into the cache */
          /* Fill in (optional exercise only) */
      } catch (IOException e) {
          System.out.println("Error writing response to client: " + e);
      }
    }
}