
package com.mageddo;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import com.mageddo.queue.CompleteQueue;
import com.mageddo.queue.QueueEnum;
import com.mageddo.utils.QueueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Configuration
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    ConfigurableBeanFactory beanFactory;

    @Autowired
    RabbitAdmin rabbitAdmin;

    @PostConstruct
    void setupQueue() {

        for(final CompleteQueue completeQueue: QueueEnum.values()){

            declareQueue(completeQueue, completeQueue, false);
            declareQueue(completeQueue, completeQueue.getDLQ(), true);

        }

    }


	/**
	 * Creates the queue
   * @param completeQueue
   * @param queue
   * @param dlq
   */
  void declareQueue(CompleteQueue completeQueue, com.mageddo.queue.Queue queue, boolean dlq){

        final Map<String, Object> arguments = new HashMap<>();
        if(!dlq){
            // when all queue retry fails move to this exchange
            arguments.put("x-dead-letter-exchange", completeQueue.getDLQ().getExchange());
            arguments.put("x-message-ttl", queue.getTTL()); // time to wait for move to DLQ
        }

        final Queue rabbitQueue = new Queue(queue.getName(), true, false, false, arguments);
        final DirectExchange exchange = new DirectExchange(queue.getExchange());
        final Binding binding = BindingBuilder.bind(rabbitQueue).to(exchange).with("");

        if(QueueUtils.queueExists(rabbitAdmin, rabbitQueue.getName())){
            if(QueueUtils.getQueueSize(rabbitAdmin, rabbitQueue.getName()) > 0){
                LOGGER.error("msg=queue already exists and is not empty");
                // MOVER para uma fila temporaria e depois trazer devolta
                return ;
            }else{
                rabbitAdmin.deleteQueue(rabbitQueue.getName());
            }
        }

        rabbitAdmin.declareQueue(rabbitQueue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(binding);

        beanFactory.registerSingleton(rabbitQueue.getName(), rabbitQueue);
        beanFactory.registerSingleton(queue.getExchange(), exchange);
        beanFactory.registerSingleton(queue.getRoutingKey(), binding);
    }

    @Bean
    RabbitAdmin getRabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

//    @Bean
//    TopicExchange exchange() {
//        return new TopicExchange("spring-boot-exchange");
//    }
//
//    @Bean
//    Binding binding(Queue queue, TopicExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(queueName);
//    }

//    @Bean
//    List<X> getList(){
//        return Arrays.asList(new X(), new X(), new X());
//    }
//
//    static class X {
//
//    }


//    @Bean
//    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
//            MessageListenerAdapter listenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(queueName);
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }
//
//    @Bean
//    Receiver receiver() {
//        return new Receiver();
//    }
//
//    @Bean
//    MessageListenerAdapter listenerAdapter(Receiver receiver) {
//        return new MessageListenerAdapter(receiver, "receiveMessage");
//    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Application.class, args);
        Thread.sleep(500000);
    }

}
