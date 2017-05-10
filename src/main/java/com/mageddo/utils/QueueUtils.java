package com.mageddo.utils;

import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.Properties;

/**
 * Created by elvis on 07/10/16.
 */
public class QueueUtils {

	public static boolean queueExists(RabbitAdmin rabbitAdmin, String queue){
		return getQueueSize(rabbitAdmin, queue) != -1;
	}

	public static int getQueueSize(RabbitAdmin rabbitAdmin, String queue){
		final Properties queueProperties = rabbitAdmin.getQueueProperties(queue);
		if (queueProperties == null){
			return -1;
		}
		return (int) queueProperties.get(RabbitAdmin.QUEUE_MESSAGE_COUNT);
	}
}
