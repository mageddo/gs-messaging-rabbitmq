package com.mageddo.receiver;

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

import java.util.Date;
import java.util.Random;

@Component
public class ColorReceiver implements Consumer<byte[]> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ColorReceiver.class);

	@Autowired
	RabbitTemplate rabbitTemplate;


//	@Scheduled(fixedDelay = 500)
	public void colorSender() {
		Date d = new Date();
		String[] keys = {QueueNames.RED_COLORS_KEY, QueueNames.BLUE_COLORS_KEY, QueueNames.ORANGE_COLORS_KEY};
		final String key = keys[new Random().nextInt(3)];
		LOGGER.info("key={}, msg={}", key, d);
		rabbitTemplate.convertAndSend(QueueNames.COLORS_EX, key, (key + "-" + d.toString()).getBytes());
	}

	@RabbitListener(queues = QueueNames.RED_COLORS, containerFactory = QueueNames.RED_COLORS + "Container")
	public void consume(byte[] bts) {

		final String msg = new String(bts);
		final int length = bts.length;
		LOGGER.info("msg={}", msg);

	}

}
