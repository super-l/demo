package cn.nepenths.demo.rabbitmq.config.bean;import lombok.Data;/** * 系统配置信息 */@Datapublic class ServiceConfigBean {    private String status;    private int    thirdNum;    private String queueName;    private int    prefetchCount;    private int    sleepTime;}