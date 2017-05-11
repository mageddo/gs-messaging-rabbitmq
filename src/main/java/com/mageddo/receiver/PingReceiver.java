package com.mageddo.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.mageddo.queue.Consumer;
import com.mageddo.queue.QueueEnum;
import com.mageddo.queue.QueueNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PingReceiver implements Consumer<String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PingReceiver.class);

  @Autowired
  private RabbitTemplate outgoingSender;

  // Scheduled task to send an object every 5 seconds
  @Scheduled(fixedDelay = 100)
  public void pingSender() {
    try{
      Date d = new Date();
      LOGGER.info("status=send, date={}", d);
      outgoingSender.convertAndSend(QueueEnum.PING.getExchange().getName(), "", new SimpleDateFormat("HH:mm:ss").format(d));
    }catch (Exception e){
      LOGGER.error("status=error");
    }
  }

  // Annotation to listen for an ExampleObject
//  @RabbitListener(queues = QueueNames.PING)
  public void consume(String date) {


    try {
      Thread.sleep(new Random().nextInt( 1000));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    if(new Random().nextInt(5) == 3){
      LOGGER.info("time={}, status=success", date);
    }else {
      LOGGER.error("time={}, status=err", date);
      throw new RuntimeException("Failed");
    }

  }

}
