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
    protected Socket clientSocket = null;
    protected ServerSocket serverSocket = null;

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

        BufferedReader fromClient = new BufferedReader(new InputStreamReader(streamFromClient));        
        request = new HttpRequest(fromClient);
//            fromClient.close();
//        System.out.println("============== " + request.toString());
        
        /* Send request to server */
        try {
            /* Open socket and write request to socket */
            server = serverSocket.accept();
            streamFromServer = server.getInputStream();
            streamToServer = server.getOutputStream();
            DataOutputStream toServer = new DataOutputStream(streamToServer);
            
            toServer.writeBytes(request.toString());
            toServer.flush();
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
            DataInputStream fromServer = new DataInputStream(streamFromServer);
            response = new HttpResponse(fromServer);
            DataOutputStream toClient = new DataOutputStream(streamToClient);
            
            /* Write response to client. First headers, then body */
            toClient.writeBytes(response.toString());
            toClient.write(response.getBody());
            toClient.flush();
            
//            toClient.close();
//            fromServer.close();
            /* Insert object into the cache */
            /* Fill in (optional exercise only) */
        } catch (IOException e) {
            System.out.println("Error writing response to client: " + e);
        } finally {
          /* Properly terminate everything after its duty */
//          if (server != null)
//            server.close();
//          if (clientSocket != null)
//            clientSocket.close();
        }
        
//        output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
//                this.serverText + " - " + time + "").getBytes());
        
//        streamToClient.close();
//        streamFromClient.close();
        
        System.out.println("##### Socket reaches its end! Request processed: " + time);
        
      } catch (IOException e) {
        //report exception somewhere.
        e.printStackTrace();
      }
    }
}