package cn.moyada.screw.socket.bio;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;

public class UDPClient implements Closeable {

    private final DatagramPacket datagramPacket;
    private final DatagramSocket datagramSocket;

    private final byte [] receive = new byte[1024];
    private final DatagramPacket receivePacket;

    public static void main(String[] args) throws SocketException, UnknownHostException {
        UDPClient udpClient = new UDPClient("127.0.0.1", 8000);
        udpClient.send("haha");
        udpClient.send("666");
    }

    public UDPClient(String host, int port) throws UnknownHostException, SocketException {

        //1、定义服务器地址、端口号、数据
        InetAddress inetAddress = InetAddress.getByName(host);

        //2、创建数据报，包含发送的信息
        datagramPacket = new DatagramPacket(new byte[1024], 1024, inetAddress, port);

        //3、创建DatagramSocket对象
        datagramSocket = new DatagramSocket();

        //1、创建数据报，用于接收服务器端响应数据，数据保存到字节数组中
        receivePacket = new DatagramPacket(receive, 1024);
    }

    public void send(String msg) {

        byte[] msgBytes = msg.getBytes();
        //4、向服务器端发送数据报
        datagramPacket.setData(msgBytes);
        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //2、接收服务器响应的数据
        try {
            datagramSocket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //3、读取数据
        String reply = new String(receive,0, receivePacket.getLength());

        System.out.println("这里是客户端，服务器端发来的消息：--"+ reply);
    }

    @Override
    public void close() {
        //4、关闭资源
        datagramSocket.close();
    }
}