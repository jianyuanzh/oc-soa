package cc.databus.user.thrift;

import cc.databus.thrift.user.UserService;
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

    public UserService.Client getUserService() {
        TSocket socket = new TSocket(serviceIp, serverPort);
        TTransport transport = new TFramedTransport(socket);

        try {
            transport.open();
        }
        catch (TTransportException e) {
            e.printStackTrace();
            return null;
        }

        TProtocol protocol = new TBinaryProtocol(transport);

        return new UserService.Client(protocol);
    }
}
