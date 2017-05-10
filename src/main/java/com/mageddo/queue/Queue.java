package com.mageddo.queue;

import org.springframework.amqp.core.Exchange;

/**
 * Created by elvis on 07/10/16.
 */
public interface Queue {

	org.springframework.amqp.core.Queue getQueue();

	Exchange getExchange();

	String getRoutingKey();

	int getTTL();

	int getConsumers();

	int getRetries();
}
