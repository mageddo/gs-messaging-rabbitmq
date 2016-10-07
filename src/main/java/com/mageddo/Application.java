
package com.mageddo;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

import com.mageddo.queue.CompleteQueue;
import com.mageddo.queue.QueueEnum;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Configuration
public class Application {

    @Autowired
    ConfigurableBeanFactory beanFactory;

    @PostConstruct
    void setupQueue() {

//        final List<Queue> queues = new ArrayList<>();
        for(final CompleteQueue completeQueue: QueueEnum.values()){

            {
                final Queue queueDLQ = new Queue(completeQueue.getDLQ().getName(), true);
                final DirectExchange exchangeDLQ = new DirectExchange(completeQueue.getDLQ().getExchange());

                beanFactory.registerSingleton(queueDLQ.getName(), queueDLQ);
                beanFactory.registerSingleton(completeQueue.getDLQ().getExchange(), exchangeDLQ);
                beanFactory.registerSingleton(completeQueue.getDLQ().getRoutingKey(),
                    BindingBuilder.bind(queueDLQ).to(exchangeDLQ).with(queueDLQ.getName()));
            }

            {
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("x-dead-letter-exchange", completeQueue.getDLQ().getExchange());
                final Queue queue = new Queue(completeQueue.getName(), true, false, false, arguments);
                final DirectExchange exchange = new DirectExchange(completeQueue.getExchange());

                beanFactory.registerSingleton(completeQueue.getName(), completeQueue);
                beanFactory.registerSingleton(completeQueue.getExchange(), exchange);
                beanFactory.registerSingleton(completeQueue.getRoutingKey(),
                    BindingBuilder.bind(queue).to(exchange).with(completeQueue.getName()));
            }

        }

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
