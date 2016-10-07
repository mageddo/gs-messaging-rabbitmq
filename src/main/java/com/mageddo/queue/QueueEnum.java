package com.mageddo.queue;

import static com.mageddo.queue.QueueNames.PING;

/**
 * Created by elvis on 07/10/16.
 */
public enum QueueEnum implements CompleteQueue {

	PING_QUEUE(PING)
	;

	private final String name;
	private final String exchange;
	private final String routingKey;
	private final DLQueue dlq;

	QueueEnum(String name) {
		this.name = name;
		this.exchange = this.name + "Exchange";
		this.routingKey = this.name + "RoutingKey";
		this.dlq = new SimpleDLQueue(this.name);
	}

	@Override
	public DLQueue getDLQ() {
		return this.dlq;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getExchange() {
		return this.exchange;
	}

	@Override
	public String getRoutingKey() {
		return this.routingKey;
	}

	static class SimpleDLQueue implements DLQueue {

		private final String name;
		private final String exchange;
		private final String routingKey;

		public SimpleDLQueue(String name) {
			this.name = name + "DLQ";
			this.exchange = this.name + "Exchange";
			this.routingKey = this.name + "RoutingKey";
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String getExchange() {
			return this.exchange;
		}

		@Override
		public String getRoutingKey() {
			return this.routingKey;
		}
	}
}
