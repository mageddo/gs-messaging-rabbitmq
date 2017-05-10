package com.mageddo.queue;

/**
 * Created by elvis on 10/05/17.
 */
public interface Consumer<T> {

	public void consume(T msg);
}
