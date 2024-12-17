package microservice.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ServiceUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUtil.class);

    private final String port;
    private String serviceAddress = null;

    @Autowired
    public ServiceUtil(@Value("${server.port}") String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public String getServiceAddress() {
        if (serviceAddress == null) {
            serviceAddress = findHostName() + "/" + findIpAddress() + ":" + port;
        }
        return serviceAddress;
    }

    private String findIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private String findHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
