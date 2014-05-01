/**
 * ProxyCache.java - Simple caching proxy
 *
 * $Id: ProxyCache.java,v 1.3 2004/02/16 15:22:00 kangasha Exp $
 *
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class ProxyCache {
    /** Port for the proxy */
    private static int port;
    /** Socket for client connections */
    private static ServerSocket socket;

    /** Create the ProxyCache object and the socket */
    public static void init(int p) {
      port = p;
      try {
          socket = new ServerSocket(port);
      } catch (IOException e) {
          System.out.println("Error creating socket: " + e);
          System.exit(-1);
      }
    }

    public static void handle(Socket client) {
      try {
        Socket server = null;
        HttpRequest request = null;
        HttpResponse response = null;
        InputStream streamFromClient = client.getInputStream();
        OutputStream streamToClient = client.getOutputStream();
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
            server = socket.accept();
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

    /** Read command line arguments and start proxy */
    public static void main(String args[]) {
      int myPort = 0;
      try {
          myPort = Integer.parseInt(args[0]);
      } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println("Need port number as argument");
          System.exit(-1);
      } catch (NumberFormatException e) {
          System.out.println("Please give port number as integer.");
          System.exit(-1);
      }
      init(myPort);
      /*
      ThreadPoolManager server = new ThreadPoolManager(socket);
      new Thread(server).start();
      */
      
      /** Main loop. Listen for incoming connections and spawn a new
       * thread for handling them */
      Socket client = null;
      
      while (true) {
        try {
          client = socket.accept();
          handle(client);
        } catch (IOException e) {
          System.out.println("Error reading request from client: " + e);
          /* Definitely cannot continue processing this request,
           * so skip to next iteration of while loop. */
          continue;
        }
      }
    }
}
