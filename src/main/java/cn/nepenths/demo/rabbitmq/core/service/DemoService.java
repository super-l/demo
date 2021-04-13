package cn.nepenths.demo.rabbitmq.core.service;

import cn.nepenths.demo.rabbitmq.config.Config;
import cn.nepenths.demo.rabbitmq.config.bean.RabbitmqConfigBean;
import cn.nepenths.demo.rabbitmq.core.message.DemoMessageHandler;
import cn.nepenths.demo.rabbitmq.utils.RabbitmqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.log4j.Log4j;

@Log4j
public class DemoService extends Thread {

    // 是否自动确认消息被成功消费
    private static final boolean ack = false;

    public void run(){
        String queueName = Config.getServiceConfig().getQueueName();
        String exchangeName = Config.getRabbitMQConfig().getExchangeName();

        log.info(String.format("正在启动PDF转图片消费服务! 队列名称:%s",queueName));

        try{
            // 获取到连接以及mq通道
            RabbitmqConfigBean rabbitmqConfigBean = Config.getRabbitMQConfig();
            RabbitmqUtil rabbitmqUtil = new RabbitmqUtil()
                    .setHost(rabbitmqConfigBean.getHost())
                    .setPort(rabbitmqConfigBean.getPort())
                    .setVirtualhost(rabbitmqConfigBean.getVirtualhost())
                    .setUser(rabbitmqConfigBean.getUser())
                    .setPassword(rabbitmqConfigBean.getPassword());

            Connection connection = rabbitmqUtil.getConnection();

            if(connection != null){
                Channel channel = connection.createChannel();
                channel.basicQos(Config.getServiceConfig().getPrefetchCount());

                // 声明exchange
                channel.exchangeDeclare(exchangeName,"direct",true);

                channel.queueDeclare(queueName, true, false, false, null);

                channel.queueBind(queueName, exchangeName, queueName);

                // 创建自定义队列消费者
                channel.basicConsume(queueName, ack, new DemoMessageHandler(channel));
            }else{
                log.error("无法连接到rabbitMQ服务器!线程即将结束!");
            }
        }catch (Exception e){
            e.printStackTrace();
            Thread t = Thread.currentThread();
            log.error(String.format("出现异常! 线程ID:%s 类:%s 提示:%s",t.getName(),e.getClass().getName(), e.getMessage()));
        }

    }

}
