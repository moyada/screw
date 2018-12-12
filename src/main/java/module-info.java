/**
 * @author xueyikang
 * @since 1.0
 **/
module screw {
    requires jol.core;
    requires mail;
    requires cglib;
    requires transitive org.slf4j;
//    provides java.lang.System.LoggerFinder
//            // put the right class name here
//            with org.log4J.Log4JSystemLoggerFinder;
    requires java.management;
    requires com.google.common;
    requires java.sql;
    requires jdk.unsupported;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires java.desktop;
    requires gson;
    requires jdk.hotspot.agent;
    requires netty;
    requires jdk.attach;
    requires jdk.management;

}