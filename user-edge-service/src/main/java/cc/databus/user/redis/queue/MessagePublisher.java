package cc.databus.user.redis.queue;

/**
 * Created by vincent on 10/04/2018.
 */
public interface MessagePublisher {
    void publish(final String message);
}
