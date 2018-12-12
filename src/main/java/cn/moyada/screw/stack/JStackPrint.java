package cn.moyada.screw.stack;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

/**
 * @author xueyikang
 * @create 2018-05-08 17:17
 */
public class JStackPrint {

    public void printStack(int pid) throws IOException, AttachNotSupportedException {
        VirtualMachine attach = VirtualMachine.attach(String.valueOf(pid));
    }
}
