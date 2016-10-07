
package com.mageddo;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import com.mageddo.queue.CompleteQueue;
import com.mageddo.queue.QueueEnum;
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

    @Autowired
    ConfigurableBeanFactory beanFactory;

    @Autowired
    RabbitAdmin rabbitAdmin;

    @PostConstruct
    void setupQueue() {

//        final List<Queue> queues = new ArrayList<>();
        for(final CompleteQueue completeQueue: QueueEnum.values()){

            declareQueue(completeQueue, completeQueue, false);
            declareQueue(completeQueue, completeQueue.getDLQ(), true);

        }

    }

    void declareQueue(CompleteQueue completeQueue, com.mageddo.queue.Queue queue, boolean dlq){

        final Map<String, Object> arguments = new HashMap<>();
        if(dlq){
        }else{
            arguments.put("x-dead-letter-exchange", completeQueue.getDLQ().getExchange());
            arguments.put("x-message-ttl", 5000);
        }

        final Queue rabbitQueue = new Queue(queue.getName(), true, false, false, arguments);
        final DirectExchange exchange = new DirectExchange(queue.getExchange());
        final Binding binding = BindingBuilder.bind(rabbitQueue).to(exchange).with("");

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
