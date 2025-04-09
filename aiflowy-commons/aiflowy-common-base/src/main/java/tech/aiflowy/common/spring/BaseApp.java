package tech.aiflowy.common.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class BaseApp implements ApplicationListener<WebServerInitializedEvent> {

    private static Integer port;
    private static ApplicationContext appContext;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        appContext = event.getApplicationContext();
        port = event.getWebServer().getPort();
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return run(new Class[]{primarySource}, args);
    }

    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        new SpringApplication(primarySources).run(args);
        printRunningAt(port);
        return (ConfigurableApplicationContext) appContext;
    }

    public static Object getBean(String name) {
        return appContext.getBean(name);
    }

    protected static void printRunningAt(int port) {
        StringBuilder msg = new StringBuilder("\nServer running at:\n");
        msg.append(" > Local  : http://localhost:").append(port).append("\n");

        List<String> ipList = getLocalIpList();
        for (String ip : ipList) {
            msg.append(" > Network: http://").append(ip).append(":").append(port).append("\n");
        }
        System.out.println(msg);
    }


    private static List<String> getLocalIpList() {
        List<String> ipList = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                NetworkInterface networkInterface = e.nextElement();
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }

                for (Enumeration<InetAddress> ele = networkInterface.getInetAddresses(); ele.hasMoreElements(); ) {
                    InetAddress ip = ele.nextElement();
                    if (ip instanceof Inet4Address) {
                        ipList.add(ip.getHostAddress());
                    }
                }
            }
            return ipList;
        } catch (Exception e) {
            return ipList;
        }
    }
}
