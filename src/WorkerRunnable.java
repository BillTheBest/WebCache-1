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
    protected Socket proxyClient = null;
    protected ServerSocket serverSocket = null;

    public WorkerRunnable(Socket clientSocket, ServerSocket serverSocket) {
        this.proxyClient = clientSocket;
        this.serverSocket = serverSocket;
    }

    public void run() {
      Socket proxyServer = null;
      String host = "google.com";
      HttpRequest request = null;
      HttpResponse response = null;
      InputStream streamFromClient = null;
      OutputStream streamToClient = null;
      InputStream streamFromServer = null;
      OutputStream streamToServer = null;
      long time = System.currentTimeMillis();
      
      /* Process request. If there are any exceptions, then simply
       * return and end this request. This unfortunately means the
       * client will hang for a while, until it timeouts. */
      
      /* Read request */
      try {
        streamFromClient = proxyClient.getInputStream();
        streamToClient = proxyClient.getOutputStream();
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(streamFromClient));        
        request = new HttpRequest(fromClient);
        host = request.getHost();
//              fromClient.close();
//          System.out.println("============== " + request.toString());
      } catch (IOException e) {
          System.out.println("Error reading request from client: " + e);
          return;
      }
      
      /* Send request to server */
      try {
          /* Open socket and write request to socket */
          proxyServer = new Socket(host, 80);
          streamFromServer = proxyServer.getInputStream();
          streamToServer = proxyServer.getOutputStream();
          DataOutputStream toServer = new DataOutputStream(streamToServer);
          toServer.writeBytes(request.toString());
          toServer.flush();
          System.out.println("###### Completed sending request to server");
//            toServer.close();
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
          System.out.println("###### Awaiting response from server...");
          DataInputStream fromServer = new DataInputStream(streamFromServer);
//          System.out.println("MID! Stream FROM server = " + streamFromServer);
//          System.out.println("MID! DIS FROM server = first byte = " + fromServer.readByte());
          response = new HttpResponse(fromServer);
          DataOutputStream toClient = new DataOutputStream(proxyClient.getOutputStream());
          
          /* Write response to client. First headers, then body */
          toClient.writeBytes(response.toString());
          toClient.write(response.getBody());
          toClient.flush();
          /* Insert object into the cache */
          /* Fill in (optional exercise only) */
      } catch (IOException e) {
          System.out.println("Error writing response to client: " + "");
          e.printStackTrace();
      } 
      
//        output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
//                this.serverText + " - " + time + "").getBytes());

      System.out.println("##### Socket reaches its end! Request processed: " + time);
      try {
        streamToClient.close();
        streamFromClient.close();
        streamToServer.close();
        streamFromServer.close();
        proxyClient.close();
        proxyServer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
}