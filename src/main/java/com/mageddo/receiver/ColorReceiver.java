package com.mageddo.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ColorReceiver {

	private static final Logger LOGGER = LoggerFactory.getLogger(ColorReceiver.class);

	// Annotation to listen for an ExampleObject
	@RabbitListener(bindings = {
		@QueueBinding(
			value = @Queue(value = "RedColors"),
			exchange = @Exchange(value = ""),
			key = "red"
		),
		@QueueBinding(
			value = @Queue(value = "BlueColors"),
			exchange = @Exchange(value = ""),
			key = "blue"
		)
	})
	public void redColorReicever(Message message) throws InterruptedException {

		final String msg = new String(message.getBody());
		msg.length();
		LOGGER.info("msg={}", msg);

	}

}
