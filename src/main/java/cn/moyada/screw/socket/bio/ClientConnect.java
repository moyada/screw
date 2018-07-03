package cn.moyada.screw.socket.bio;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnect implements Closeable {

    private final Socket socket;

    private final OutputStream out;
    private final InputStream in;

    private final byte[] recvBuf = new byte[1024];
    private int recvMsgSize;

    public ClientConnect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = socket.getOutputStream();
        in = socket.getInputStream();
    }

    public static void main(String[] args) throws IOException {
        ClientConnect client = new ClientConnect("127.0.0.1", 5443);
        client.send("hahaha");
        client.send("6666");
        client.close();
    }

    public void send(String msg){
        try {
            out.write(msg.getBytes());
            out.flush();

            do {
                recvMsgSize = in.read(recvBuf);
                System.out.println(new String(recvBuf, 0, recvMsgSize));
            }
            while (recvMsgSize == 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if(!socket.isClosed()) {
                socket.close();
            }
            if(out != null){
                out.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
