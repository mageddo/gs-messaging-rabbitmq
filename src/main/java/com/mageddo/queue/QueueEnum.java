package com.mageddo.queue;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by elvis on 07/10/16.
 */
public enum QueueEnum implements com.mageddo.queue.Queue {

	PING(new Queue(QueueNames.PING), 10000, 2),
	RED_COLORS(new Queue(QueueNames.RED_COLORS), new TopicExchange(QueueNames.COLORS_EX), QueueNames.RED_COLORS_KEY, 10000, 2, 2),

	;

	private DLQueue dlq;
	private Queue queue;
	private Exchange exchange;
	private String routingKey;
	private int ttl;
	private int retries;
	private int consumers;

	QueueEnum(Queue queue, int ttl, int retries) {
		this.dlq = new SimpleDLQueue(queue.getName());
		set(queue, new DirectExchange(queue.getName() + "Exchange"), "", ttl, retries, 1);
	}

	QueueEnum(Queue queue, Exchange exchange, String routingKey, int ttl, int retries, int consumers) {
		set(queue, exchange, routingKey, ttl, retries, consumers);
	}

	private void set(Queue queue, Exchange exchange, String routingKey, int ttl, int retries, int consumers) {

		this.exchange = exchange;
		this.routingKey = routingKey;
		this.ttl = ttl;
		this.retries = retries;
		this.consumers = consumers;
		this.dlq = new SimpleDLQueue(queue.getName());

		// when all queueEnum retry fails move to this exchange
		final Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-dead-letter-exchange", getDlq().getExchange().getName());
//		arguments.put("x-message-ttl", getTTL()); // time to wait for move to DLQ
		this.queue = new Queue(queue.getName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(), arguments);
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
		public int getRetries() {
			return 0;
		}

	}
}
