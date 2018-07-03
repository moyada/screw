package cn.moyada.screw.socket.bio;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer implements Closeable  {

    private final DatagramSocket datagramSocket;

    //创建字节数组，指定接受的数据报的大小
    private final byte [] data = new byte[ 1024];
    private final DatagramPacket datagramPacket;

    public static void main(String[] args) throws SocketException {
        new UDPServer(8000).start();
    }

    public UDPServer(int port) throws SocketException {
        //1、创建服务器端DatagramSocket,指定端口
        datagramSocket = new DatagramSocket(port);

        //2、创建数据报，用于接受客户端发送的数据
        datagramPacket = new DatagramPacket(data, data.length);
    }

    public void start() {
        //3、接收客户端发送的数据
        System.out.println("服务器已经开启，等待客户端的连接");

        while (true) {
            //此方法在接收到数据之前会一直阻塞
            try {
                datagramSocket.receive(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //4、读取客户端发送的数据
            //参数： data 要转换的数组 0 从数组的下标0 开始  datagramPacket.getLength() 长度为接收到的长度
            String info = new String(data, 0, datagramPacket.getLength());
            System.out.println("这里是服务器，客户端发来的消息：--" + info);

            /**
             *  向客户端进行响应
             */
            //1、定义客户端的地址、端口号、数据
            //获取客户端 ip地址
            InetAddress inetAddress = datagramPacket.getAddress();
            //获取客户端端口号
            int port = datagramPacket.getPort();
            //将要响应的内容保存到byte数组中
            byte[] receive = "欢迎您！".getBytes();
            //2创建数据报，包含响应的数据信息
            DatagramPacket datagramPacket12 = new DatagramPacket(receive, receive.length, inetAddress, port);
            //3、响应客户端
            try {
                datagramSocket.send(datagramPacket12);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        //4、关闭资源
        datagramSocket.close();
    }
}