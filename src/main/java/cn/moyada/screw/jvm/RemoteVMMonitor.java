package cn.moyada.screw.jvm;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class RemoteVMMonitor extends AbstractVMMonitor {

    public RemoteVMMonitor(String host, int port, String domain) throws IOException {
        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":"+ port + "/" + domain;
        JMXServiceURL serviceURL = new JMXServiceURL(url);
        JMXConnector conn = JMXConnectorFactory.connect(serviceURL);
        MBeanServerConnection connection = conn.getMBeanServerConnection();

        ObjectName mbeanName = null;
        try {
            mbeanName = new ObjectName("jmxBean:name=hello");
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        DynamicMBean dynamicMBean = MBeanServerInvocationHandler.newProxyInstance(connection, mbeanName, DynamicMBean.class, true);

        MemoryMXBean memBean = ManagementFactory.newPlatformMXBeanProxy
                (connection, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);

    }
}
