package com.mageddo.receiver;

import java.util.Date;
import java.util.Random;

import com.mageddo.queue.QueueNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PingReceiver {

  private static final Logger LOGGER = LoggerFactory.getLogger(PingReceiver.class);

  @Autowired
  private RabbitTemplate outgoingSender;

  // Scheduled task to send an object every 5 seconds
  @Scheduled(fixedDelay = 2000)
  public void sender() {
    Date d = new Date();
    LOGGER.info("Sending example object at " + d);
    outgoingSender.convertAndSend(d);
  }

  // Annotation to listen for an ExampleObject
  @RabbitListener(queues = QueueNames.PING)
  public void handleMessage(Date date) {

    if(new Random().nextBoolean()){
      LOGGER.info("Received incoming object at " + date);
    }else {
      LOGGER.error("error at consuming");
      throw new RuntimeException("Failed");
    }

  }

}
