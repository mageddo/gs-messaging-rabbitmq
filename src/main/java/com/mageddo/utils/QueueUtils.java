package com.mageddo.utils;

import org.springframework.amqp.rabbit.core.RabbitAdmin;

/**
 * Created by elvis on 07/10/16.
 */
public class QueueUtils {

	public static boolean queueExists(RabbitAdmin rabbitAdmin, String queue){
		return rabbitAdmin.getQueueProperties(queue).get(RabbitAdmin.QUEUE_MESSAGE_COUNT) != null;
	}

	public static int getQueueSize(RabbitAdmin rabbitAdmin, String queue){
		return (int) rabbitAdmin.getQueueProperties(queue).get(RabbitAdmin.QUEUE_MESSAGE_COUNT);
	}
}
