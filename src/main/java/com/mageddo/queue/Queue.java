package com.mageddo.queue;

/**
 * Created by elvis on 07/10/16.
 */
public interface Queue {

	String getName();
	String getExchange();
	String getRoutingKey();
	int getTTL();
}
