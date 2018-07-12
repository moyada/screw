package cn.moyada.screw.net.telnet;

import cn.moyada.screw.utils.AssertUtil;
import cn.moyada.screw.utils.JVMUtil;
import cn.moyada.screw.utils.StringUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

/**
 * @author xueyikang
 * @create 2018-07-12 15:38
 */
public class JVMConnect implements Closeable {

    private final ServerSocket serverSocket;

    private final String password;

    public JVMConnect(int port, String password) throws IOException {
        AssertUtil.checkPort(port);
        this.serverSocket = new ServerSocket(port);
        this.password = password;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void listener() {
        while(true) {
            Socket client;
            try {
                client = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            try {
                client.setSoTimeout(60_000);
                handle(client);
            } catch (IOException e) {
//                e.printStackTrace();
            } finally{
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void handle(Socket client) throws IOException {
        boolean checkAuth = false;
        BufferedReader reader;
        String line;

        SocketAddress clientAddress = client.getRemoteSocketAddress();
        System.out.println("Handling client at "+clientAddress);

        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        OutputStream out = client.getOutputStream();
        print(out, "please enter password: ");

        while((line = reader.readLine()) != null) {
//            System.out.println("accept: " + line);

            if(!checkAuth) {
                checkAuth = checkPassword(line);

                if(checkAuth) {
                    print(out, "command > ");
                } else {
                    print(out, "password error, retry.\n");
                    print(out, "please enter password: ");
                }
                continue;
            }
            String result = processCommand(line);
            if(null != result) {
                print(out, result);
            }
            print(out, "command > ");
        }
    }

    private boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    private void print(OutputStream out, String msg) throws IOException {
        out.write(msg.getBytes());
        out.flush();
    }

    private String processCommand(String command) throws IOException {
        switch (command.toLowerCase()) {
            case "thread":
            case "t":
                return JVMUtil.getAllThread();
            case "heap":
            case "h":
                return JVMUtil.getHeap() + "\n";
            case "non heap":
            case "n":
                return JVMUtil.getNonHeap() + "\n";
            case "exit":
            case "q":
                throw new IOException("QUIT");
        }
        return null;
    }

    @Override
    public void close() {
        try {
            if(!serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("please input telnet port: ");
        String port = scanner.nextLine();
        int inPort = Integer.parseInt(port);
        System.out.print("please input telnet password: ");
        String password = scanner.nextLine();
        if (StringUtil.isEmpty(password)) {
            throw new NullPointerException("password can not be null.");
        }
        JVMConnect connect = new JVMConnect(inPort, password);
        System.out.println("server start.");
        connect.listener();
    }
}
