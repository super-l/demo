package cn.nepenths.demo.rabbitmq.utils;
/*
    rabbitmq操作
    Created by superl@nepenthes.cn on 2016-12-25.
*/
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitmqUtil {

    private String host;
    private String port;
    private String virtualhost;
    private String user;
    private String password;

    private String exchangeName;
    private String exchangeType = "direct";
    private String queueName;
    private String routeKey;

    // 死信队列相关
    private int    deadLetterTimeOut = 10*1000;
    private String deadLetterQueueName;
    private String deadLetterToQueueName;

    public RabbitmqUtil(){}

    public RabbitmqUtil(String host, String port, String virtualhost, String user, String password){
        this.host = host;
        this.port = port;
        this.virtualhost = virtualhost;
        this.user = user;
        this.password = password;
    }

    public RabbitmqUtil setHost(String host) {
        this.host = host;
        return this;
    }

    public RabbitmqUtil setPort(String port) {
        this.port = port;
        return this;
    }

    public RabbitmqUtil setVirtualhost(String virtualhost) {
        this.virtualhost = virtualhost;
        return this;
    }

    public RabbitmqUtil setUser(String user) {
        this.user = user;
        return this;
    }

    public RabbitmqUtil setPassword(String password) {
        this.password = password;
        return this;
    }

    public RabbitmqUtil setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
        return this;
    }

    public RabbitmqUtil setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
        return this;
    }

    public RabbitmqUtil setQueueName(String queueName) {
        this.queueName = queueName;
        return this;
    }

    public RabbitmqUtil setRouteKey(String routeKey) {
        this.routeKey = routeKey;
        return this;
    }

    public RabbitmqUtil setDeadLetterQueueName(String deadLetterQueueName) {
        this.deadLetterQueueName = deadLetterQueueName;
        return this;
    }

    public RabbitmqUtil setDeadLetterToQueueName(String deadLetterToQueueName) {
        this.deadLetterToQueueName = deadLetterToQueueName;
        return this;
    }

    public RabbitmqUtil setDeadLetterTimeOut(int deadLetterTimeOut) {
        this.deadLetterTimeOut = deadLetterTimeOut;
        return this;
    }

    /**
     * 连接
     */
    public Connection getConnection() throws IOException, TimeoutException {
        Connection connection = null;
        if ( this.host == null || this.port == null || this.virtualhost == null || this.user == null || this.password == null) {
            System.out.println("rabbitmq连接信息参数设置不完整");
            return connection;
        }

        //定义连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(this.host);   //设置服务地址
        factory.setPort(Integer.parseInt(this.port));   //端口
        factory.setConnectionTimeout(30000);    // 超时时间

        //设置账号信息,用户名、密码、vhost
        factory.setVirtualHost(this.virtualhost);
        factory.setUsername(this.user);
        factory.setPassword(this.password);

        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(10000);

        connection = factory.newConnection();
        return connection;
    }


    /**
     * 关闭通道和连接
     * @param connection
     * @throws Exception
     */
    public void closeConnection(Connection connection) throws IOException {
        if(connection != null){
            connection.close();
        }
    }


    /**
     * 向队列发送任务消息
     * @throws Exception
     */
    public void sendMessage(String message) throws IOException, TimeoutException {
        if ( this.exchangeName == null || this.queueName == null ){
            System.out.println("交换机名称或队列名称未设置!");
            return;
        }

        if ( this.routeKey == null ){
            this.routeKey = this.queueName;
        }

        Connection connection = getConnection();
        if ( connection != null ){
            // 创建管道
            Channel channel = connection.createChannel();

            // 声明exchange
            channel.exchangeDeclare(this.exchangeName,this.exchangeType,true);

            // 声明队列
            channel.queueDeclare(this.queueName,true,false,false,null);

            // 绑定
            channel.queueBind(this.queueName, exchangeName, this.routeKey);

            // 发送消息
            channel.basicPublish(exchangeName, this.queueName, null, message.getBytes());

            channel.close();
            closeConnection(connection);
        }
    }


    /**
     * 向主动查询队列发送任务消息
     * @throws Exception
     */
    public void sendDelayMessage(String message) throws IOException, TimeoutException {
        if ( this.exchangeName == null || this.deadLetterToQueueName == null || this.deadLetterQueueName == null ){
            System.out.println("交换机名称或死信队列名称未设置!");
            return;
        }

        Connection connection = getConnection();
        if ( connection != null ){
            // 创建管道
            Channel channel = connection.createChannel();

            // 声明exchange
            channel.exchangeDeclare(this.exchangeName,this.exchangeType,true);

            // 声明
            Map<String, Object> args = new HashMap<String, Object>(2);
            args.put("x-dead-letter-exchange", this.exchangeName);
            args.put("x-dead-letter-routing-key", this.deadLetterToQueueName);
            args.put("x-message-ttl", this.deadLetterTimeOut);
            channel.queueDeclare(this.deadLetterQueueName,true,false,false,args);

            channel.queueBind(this.deadLetterQueueName, this.exchangeName, this.deadLetterQueueName);

            // 发送消息
            channel.basicPublish(this.exchangeName, this.deadLetterQueueName, null, message.getBytes());

            channel.close();
            closeConnection(connection);
        }

    }


}
