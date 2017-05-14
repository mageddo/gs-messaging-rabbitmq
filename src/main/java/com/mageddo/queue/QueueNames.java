package com.mageddo.queue;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;

/**
 * Created by elvis on 07/10/16.
 */
public class QueueNames {

	public static final String PING = "Ping";
	public static final String RED_COLORS = "RedColors";
	public static final String RED_COLORS_KEY = "red";
	public static final String BLUE_COLORS_KEY = "blue";
	public static final String ORANGE_COLORS_KEY = "orange";
	public static final String COLORS_EX = "ColorsTopicExchange";
	public static final String MAIL = "Mail";

	private QueueNames() {
	}
}
