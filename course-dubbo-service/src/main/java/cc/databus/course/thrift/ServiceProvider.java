package cc.databus.course.thrift;

import cc.databus.thrift.user.UserService;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceProvider {
    @Value("${thrift.user.ip}")
    private String serviceIp;
    @Value("${thrift.user.port}")
    private int serverPort;

    private enum ServiceType {
        USER,
    }

    public UserService.Client getUserService() {
        return getService(serviceIp, serverPort, ServiceType.USER);
    }



    private <T> T getService(String ip, int port, ServiceType type) {
        TSocket socket = new TSocket(ip, port);
        TTransport transport = new TFramedTransport(socket);

        try {
            transport.open();
        }
        catch (TTransportException e) {
            e.printStackTrace();
            return null;
        }

        TProtocol protocol = new TBinaryProtocol(transport);

        TServiceClient client = null;
        switch (type) {
            case USER:
                client = new UserService.Client(protocol);
                break;

        }
        return (T) client;

    }

}
