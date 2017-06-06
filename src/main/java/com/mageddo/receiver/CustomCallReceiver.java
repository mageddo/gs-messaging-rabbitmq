package com.mageddo.receiver;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mageddo.queue.QueueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author elvis
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 6/6/17 4:37 PM
 */
@Component
public class CustomCallReceiver implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private AtomicInteger counter = new AtomicInteger(0);

    @Scheduled(fixedDelay = 3000)
    public void newCall() throws JsonProcessingException {

        final CallVO msg = new CallVO(counter.incrementAndGet());
        final Exchange callExchange = getCallExchange();
        rabbitTemplate.convertAndSend(
            callExchange.getName(), "",
             new Random().nextBoolean() ? objectMapper.writeValueAsString(msg) : objectMapper.writeValueAsBytes(msg)
        );

    }

//    @RabbitListener(containerFactory = "callFactory", queues = "callQueue")
    @Override
    public void onMessage(Message message) {
        try {
            logger.info("rawMsg={}, parsedVO={}", new String(message.getBody()), objectMapper.readValue(message.getBody(), CallVO.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Bean("callFactory")
    public SimpleRabbitListenerContainerFactory factory(ConnectionFactory connectionFactory){
        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(1);
        return factory;
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, CustomCallReceiver receiver)
            throws NoSuchMethodException {

        final Queue callQueue = getCallQueue();
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setQueues(callQueue);
        container.setMessageListener(receiver);
        container.setMessageConverter(new SimpleMessageConverter());
        container.start();
        
        return container;
    }

    @Bean
    public Binding queueBinding(){
        return BindingBuilder.bind(getCallQueue()).to(getCallExchange()).with("").noargs();
    }

    @Bean
    public Queue getCallQueue() {
        return QueueEnum.CALL.getQueue();
    }

    @Bean
    public Exchange getCallExchange() {
        return QueueEnum.CALL.getExchange();
    }

    static class CallVO {

        int number;

        public CallVO() {
        }

        public CallVO(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder()//
                    .append("CallVO [")//
                    .append("number=")//
                    .append(number)//
                    .append("]");
            return builder.toString();
        }
    }

}
