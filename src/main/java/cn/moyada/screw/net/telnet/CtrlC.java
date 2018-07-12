package cn.moyada.screw.net.telnet;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author xueyikang
 * @create 2018-07-12 17:00
 */
public class CtrlC implements KeyListener {
    private Process process;
    static JTextArea area;
    boolean t1 = false, t2 = false;

    public CtrlC() {
    }

    public void listnerCommand(String command) throws IOException {
        this.process = Runtime.getRuntime().exec(command);
        JFrame frame = new JFrame();
        area = new JTextArea();
        area.addKeyListener(this);
        frame.add(area);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(false);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_CONTROL)
            t1=true;
        if(e.getKeyCode()==KeyEvent.VK_C)
            t2=true;
        if(t1 && t2)
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            try {
                bufferedWriter.write((char)3);
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            t1=false;
            t2=false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_CONTROL)
            t1=false;
        if(e.getKeyCode()==KeyEvent.VK_C)
            t2=false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}