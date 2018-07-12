package cn.moyada.screw.net.socket.bio;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author xueyikang
 * @create 2018-04-10 23:02
 */
public class TCPServer implements Closeable {

    private final ServerSocket serverSocket;

    private InputStream in = null;
    private OutputStream out = null;

    public TCPServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public static void main(String[] args) throws IOException {
        new TCPServer(5443).start();
    }

    public void start(){
        byte[] recvBuf = new byte[1024];
        int recvMsgSize;
        try {
            while(true) {
                Socket clntSocket = serverSocket.accept();

                SocketAddress clientAddress = clntSocket.getRemoteSocketAddress();
                System.out.println("Handling client at "+clientAddress);

                in = clntSocket.getInputStream();
                while((recvMsgSize = in.read(recvBuf)) != -1) {
                    System.out.println(new String(recvBuf, 0, recvMsgSize));

                    OutputStream out = clntSocket.getOutputStream();
                    out.write("receive done".getBytes());
                    out.flush();
                }

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            close();
        }
    }

    @Override
    public void close() {
        try {
            if(!serverSocket.isClosed()) {
                serverSocket.close();
            }
            if(in != null){
                in.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
