
package com.mageddo;

import com.mageddo.queue.*;
import com.mageddo.receiver.ColorReceiver;
import com.mageddo.receiver.PingReceiver;
import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

/**
 * https://github.com/mageddo/gs-messaging-rabbitmq/blob/master/src/main/java/com/mageddo/Application.java
 * https://github.com/mageddo/gs-messaging-rabbitmq/blob/master/src/main/java/com/mageddo/receiver/PingReceiver.java
 * https://github.com/spring-guides/gs-messaging-rabbitmq/blob/master/complete/src/main/java/hello/Application.java
 * https://github.com/spring-guides/gs-messaging-rabbitmq/blob/master/complete/src/main/java/hello/Receiver.java
 */
@SpringBootApplication
@EnableScheduling
@Configuration
public class Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	@Autowired
	ConfigurableBeanFactory beanFactory;

	@Autowired
	CachingConnectionFactory connectionFactory;

	@Autowired
	RabbitAdmin rabbitAdmin;

	@Autowired
	RabbitTemplate rabbitTemplate;


	@PostConstruct
	void setupQueue() {
		for (final QueueEnum completeQueue : QueueEnum.values()) {

			declareQueue(completeQueue, completeQueue.getDlq());
			declareQueue(completeQueue.getDlq(), null);

		}

	}


	/**
	 * Creates the queueEnum
	 *
	 * @param
	 * @param queueEnum
	 * @param dlq
	 */
	void declareQueue(Queue queueEnum, DLQueue dlq) {

		final Binding binding = BindingBuilder.bind(queueEnum.getQueue())
			.to(queueEnum.getExchange())
			.with(queueEnum.getRoutingKey())
			.noargs();


		rabbitAdmin.declareQueue(queueEnum.getQueue());
		rabbitAdmin.declareExchange(queueEnum.getExchange());
		rabbitAdmin.declareBinding(binding);

		beanFactory.registerSingleton(queueEnum.getQueue().getName(), queueEnum.getQueue());
		beanFactory.registerSingleton(queueEnum.getExchange().getName(), queueEnum.getExchange());

		if (!(queueEnum instanceof DLQueue)) {

			final SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
			containerFactory.setConnectionFactory(connectionFactory);
			containerFactory.setConcurrentConsumers(queueEnum.getConsumers());
			containerFactory.setMaxConcurrentConsumers(queueEnum.getMaxConsumers());


			final RetryOperationsInterceptor interceptorBuilder = RetryInterceptorBuilder
				.stateless()
				.backOffOptions(queueEnum.getTTL(), 2, queueEnum.getTTL())
				.maxAttempts(queueEnum.getRetries())
				.recoverer(new RepublishMessageRecoverer(rabbitTemplate, dlq.getExchange().getName(), dlq.getRoutingKey()))
				.build();

			containerFactory.setAdviceChain(interceptorBuilder);

			beanFactory.registerSingleton(queueEnum.getQueue().getName() + "Container", containerFactory);

		}

	}

	@Bean
	@Primary
	public RabbitTemplate rabbitTemplate(){
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setChannelTransacted(true);
		return rabbitTemplate;
	}


	@Bean
	public RabbitAdmin getRabbitAdmin() {
		final RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		rabbitAdmin.getRabbitTemplate().setChannelTransacted(true);
		return rabbitAdmin;
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(Application.class, args);
	}

}
