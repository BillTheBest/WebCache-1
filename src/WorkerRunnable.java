import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

/* Credit: this code is inspired from 
 * http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html
 */
public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;
    protected String serverText = null;

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.serverText = "HI HI HI";
    }

    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            long time = System.currentTimeMillis();
            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
                    this.serverText + " - " +
                    time +
                    "").getBytes());
            output.close();
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}