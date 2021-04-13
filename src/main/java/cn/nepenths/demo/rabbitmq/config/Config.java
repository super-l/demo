package cn.nepenths.demo.rabbitmq.config;

import cn.nepenths.demo.rabbitmq.config.bean.RabbitmqConfigBean;
import cn.nepenths.demo.rabbitmq.config.bean.ServiceConfigBean;
import lombok.extern.log4j.Log4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

@Log4j
public class Config {

    private static String filePath = "config/config.xml";

    private static Document document;

    public static ServiceConfigBean serviceConfigBean;

    public static RabbitmqConfigBean rabbitmqConfigBean;


    static {
        document = getDocument();
        if (document != null) {
            serviceConfigBean = getServiceConfig();
            rabbitmqConfigBean = getRabbitMQConfig();
        }else{
            log.error("载入配置信息失败!");
            System.exit(0);
        }
    }

    private static Document getDocument(){
        SAXReader saxReader = new SAXReader();
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("配置文件不存在!");
        }
        try {
            document = saxReader.read(file);
            return document;
        } catch (DocumentException e) {
            log.error(String.format("读取xml配置异常!提示:%s",e.getMessage()));
        }
        return null;
    }


    public static ServiceConfigBean getServiceConfig(){
        if ( serviceConfigBean != null){
            return serviceConfigBean;
        }
        ServiceConfigBean serviceConfigBean = new ServiceConfigBean();

        // 通过document对象获取根元素的信息
        Element rootEle = document.getRootElement();

        // 根据元素名字获取根元素下某个子元素
        Element ele = rootEle.element("service");

        serviceConfigBean.setStatus(ele.elementText("status"));
        serviceConfigBean.setThirdNum(Integer.parseInt(ele.elementText("third_num")));
        serviceConfigBean.setQueueName(ele.elementText("queue_name"));
        serviceConfigBean.setPrefetchCount(Integer.parseInt(ele.elementText("prefetch_count")));
        serviceConfigBean.setSleepTime(Integer.parseInt(ele.elementText("sleep_time")));
        return serviceConfigBean;
    }

    /**
     * 获取rabbitmq配置信息
     * @return
     */
    public static RabbitmqConfigBean getRabbitMQConfig(){
        if ( rabbitmqConfigBean != null ){
            return rabbitmqConfigBean;
        }
        RabbitmqConfigBean config = new RabbitmqConfigBean();

        Element rootEle = document.getRootElement();
        Element ele = rootEle.element("rabbitmq");

        config.setHost(ele.elementText("host"));
        config.setPort(ele.elementText("port"));
        config.setUser(ele.elementText("username"));
        config.setPassword(ele.elementText("password"));
        config.setVirtualhost(ele.elementText("virtualhost"));
        config.setExchangeName(ele.elementText("exchange_name"));
        return config;
    }

}
