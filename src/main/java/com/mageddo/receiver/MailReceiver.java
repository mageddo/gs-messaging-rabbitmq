package com.mageddo.receiver;

import com.mageddo.queue.QueueEnum;
import com.mageddo.queue.QueueNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by elvis on 13/05/17.
 */
@Component
public class MailReceiver {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailReceiver.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private int id = 0;
	private AtomicInteger counter = new AtomicInteger(0);

	@Scheduled(fixedDelay = Integer.MAX_VALUE)
	public void postMail() {
		for(;;){
			final StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			rabbitTemplate.convertAndSend(QueueEnum.MAIL.getExchange().getName(), "", String.format("Hello %04d", ++id));
			LOGGER.info("status=success, time={}", stopWatch.getTotalTimeMillis());
		}
	}

	@RabbitListener(queues = QueueNames.MAIL, containerFactory = QueueNames.MAIL + "Container")
	public void consume(String email) throws InterruptedException {

//		if (new Random().nextInt(30) == 3) {
//		LOGGER.info("status=mail-received, status=begin, mail={}", email);
		boolean error = false;
		if (!error) {
			Thread.sleep(50);
			LOGGER.info("status=mail-received, status=success, counter={}, mail={}", counter.incrementAndGet(), email);
		} else {
			LOGGER.error("status=mail-received, status=error, mail={}", email);
			throw new RuntimeException("failed");
		}
	}



}
