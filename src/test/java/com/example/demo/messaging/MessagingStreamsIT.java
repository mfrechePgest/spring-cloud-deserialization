package com.example.demo.messaging;

import com.example.demo.Testcontainers;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.core.io.ClassPathResource;
import org.testcontainers.containers.RabbitMQContainer;

import java.time.Duration;

@SpringBootTest
@ImportTestcontainers(Testcontainers.class)
public class MessagingStreamsIT {

    @Autowired
    private MessagingStreams messagingStreams;

    @BeforeEach
    public void reset() {
        messagingStreams.resetResponse();
    }

    @Test
    public void testWhenAllFieldsMatch() throws Exception {
        test("message.json", "sample.exchange");
    }

    @Test
    public void testWhenTheresMoreFieldInMessage() throws Exception {
        test("message2.json", "sample.exchange");
    }

    @Test
    public void testWhenJsonIgnoreAllFieldsMatch() throws Exception {
        test("message.json", "sample.exchange2");
    }

    @Test
    public void testWhenJsonIgnoreAndMoreFieldInMessage() throws Exception {
        test("message2.json", "sample.exchange2");
    }

    private void test(String path, String exchange) throws Exception {
        RabbitTemplate rabbitTemplate = rabbitTestTemplate();
        rabbitTemplate.setExchange(exchange);
        rabbitTemplate.setRoutingKey("myRoutingKey");
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");

        rabbitTemplate.send(
                new Message(new ClassPathResource(path).getContentAsByteArray(),
                        messageProperties
                )
        );

        Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .until(() -> messagingStreams.getLastResponse() != null);


    }


    private RabbitTemplate rabbitTestTemplate() throws Exception {
        final RabbitConnectionFactoryBean rabbitConnectionFactoryBean =
                new RabbitConnectionFactoryBean();
        RabbitMQContainer rabbit = Testcontainers.rabbitMQContainer;
        rabbitConnectionFactoryBean.setHost(rabbit.getHost());
        rabbitConnectionFactoryBean.setPort(rabbit.getAmqpPort());
        rabbitConnectionFactoryBean.setUsername(rabbit.getAdminUsername());
        rabbitConnectionFactoryBean.setPassword(rabbit.getAdminPassword());
        rabbitConnectionFactoryBean.setEnableHostnameVerification(false);
        rabbitConnectionFactoryBean.afterPropertiesSet();

        final CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(rabbitConnectionFactoryBean.getObject());
        return new RabbitTemplate(connectionFactory);
    }

}
