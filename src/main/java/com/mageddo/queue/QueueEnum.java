package com.mageddo.queue;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by elvis on 07/10/16.
 */
public enum QueueEnum implements com.mageddo.queue.Queue {

	MAIL(new Queue(QueueNames.MAIL), new TopicExchange(QueueNames.MAIL + "Exchange"), "", 2000, 2, 10, 10),
	PING(new Queue(QueueNames.PING), 10000, 2),
	RED_COLORS(new Queue(QueueNames.RED_COLORS), new TopicExchange(QueueNames.COLORS_EX), QueueNames.RED_COLORS_KEY, 10000, 2, 1, 2),

	;

	private DLQueue dlq;
	private Queue queue;
	private Exchange exchange;
	private String routingKey;
	private int ttl;
	private int retries;
	private int consumers;
	private int maxConsumers;

	QueueEnum(Queue queue, int ttl, int retries) {
		this.dlq = new SimpleDLQueue(queue.getName());
		set(queue, new DirectExchange(queue.getName() + "Exchange"), "", ttl, retries, 1, 1);
	}

	QueueEnum(Queue queue, Exchange exchange, String routingKey, int ttl, int retries, int consumers, int maxConsumers) {
		set(queue, exchange, routingKey, ttl, retries, consumers, maxConsumers);
	}

	private void set(Queue queue, Exchange exchange, String routingKey, int ttl, int retries, int consumers, int maxConsumers) {

		this.exchange = exchange;
		this.routingKey = routingKey;
		this.ttl = ttl;
		this.retries = retries;
		this.consumers = consumers;
		this.maxConsumers = maxConsumers;
		this.dlq = new SimpleDLQueue(queue.getName());
		this.queue = queue;
	}



	public DLQueue getDlq() {
		return dlq;
	}

	public Queue getQueue() {
		return queue;
	}

	public Exchange getExchange() {
		return exchange;
	}

	@Override
	public int getRetries() {
		return retries;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public int getTTL() {
		return ttl;
	}

	public int getConsumers() {
		return consumers;
	}

	@Override
	public int getMaxConsumers() {
		return maxConsumers;
	}

	class SimpleDLQueue implements DLQueue {

		private final Queue queue;
		private final DirectExchange exchange;
		private final String routingKey;

		public SimpleDLQueue(String name) {
			this.queue = new Queue(name + "DLQ");
			this.exchange = new DirectExchange(getQueue().getName() + "Exchange");
			this.routingKey = "";
		}

		@Override
		public Queue getQueue() {
			return this.queue;
		}

		@Override
		public DirectExchange getExchange() {
			return this.exchange;
		}

		@Override
		public String getRoutingKey() {
			return this.routingKey;
		}

		@Override
		public int getTTL() {
			return 0;
		}

		@Override
		public int getConsumers() {
			return 1;
		}

		@Override
		public int getMaxConsumers() {
			return 1;
		}

		@Override
		public int getRetries() {
			return 0;
		}

	}
}
