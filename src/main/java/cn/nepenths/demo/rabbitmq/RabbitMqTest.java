package cn.nepenths.demo.rabbitmq;

import cn.nepenths.demo.rabbitmq.config.Config;
import cn.nepenths.demo.rabbitmq.core.service.DemoService;
import lombok.extern.log4j.Log4j;

@Log4j
public class RabbitMqTest {

    public static void main(String[] args) {
        new RabbitMqTest().go();
    }

    private void go() {
        log.debug("正在启动核心消费端...");

        this.bookTransService();
    }

    private void bookTransService(){
        if("true".equals(Config.getServiceConfig().getStatus())){
            int thirdNum = Config.getServiceConfig().getThirdNum();
            log.debug(String.format("消费端线程数量:%d",thirdNum));

            for ( int i = 0; i<= thirdNum-1; i++){
                Thread payforThread = new Thread(new DemoService());
                payforThread.start();
            }
        }
    }

}
