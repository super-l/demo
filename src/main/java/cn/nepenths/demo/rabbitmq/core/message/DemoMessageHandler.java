package cn.nepenths.demo.rabbitmq.core.message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.log4j.Log4j;

@Log4j
public class DemoMessageHandler extends DefaultConsumer {

    private Channel channel;

    public DemoMessageHandler(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        String message = new String(body);
        if ( "".equals(message)){
            this.basicAckQueueMessage(envelope);
            return;
        }

        String logmsg = String.format("[提示]收到消息:%s",message);
        log.info(logmsg);

        try {
            this.basicAckQueueMessage(envelope);
        } catch (Exception e) {
            log.error(String.format("[提示]发现异常!提示:%s", e.getMessage()));
        }
    }

    /**
     * 手工确认消费
     * @param envelope
     */
    private void basicAckQueueMessage(Envelope envelope){
        try{
            channel.basicAck(envelope.getDeliveryTag(), false);
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.format("消费确认异常!提示:%s",e.getMessage()));
        }
    }
}
