package com.mageddo.receiver;

import com.mageddo.queue.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ColorReceiver implements Consumer<byte[]> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ColorReceiver.class);

//	// Annotation to listen for an ExampleObject
//	@RabbitListener(bindings = {
//		@QueueBinding(
//			value = @Queue(value = "RedColors"),
//			exchange = @Exchange(value = ""),
//			key = "red"
//		),
//		@QueueBinding(
//			value = @Queue(value = "BlueColors"),
//			exchange = @Exchange(value = ""),
//			key = "blue"
//		)
//	})
	public void consume(byte[] bts) {

		final String msg = new String(bts);
		final int length = bts.length;
		LOGGER.info("msg={}", msg);

	}

}
