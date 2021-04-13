package cn.nepenths.demo.rabbitmq.config.bean;

import lombok.Data;

@Data
public class RabbitmqConfigBean {

    private String host;

    private String port;

    private String user;

    private String password;

    private String virtualhost;

    private String exchangeName;

}
