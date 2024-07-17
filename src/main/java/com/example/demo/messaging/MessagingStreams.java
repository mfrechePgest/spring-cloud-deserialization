package com.example.demo.messaging;

import com.example.demo.messaging.dto.MessageResponse;
import com.example.demo.messaging.dto.MessageWithJsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service
public class MessagingStreams {

    private Object response;

    public void resetResponse() {
        this.response = null;
    }

    @Bean
    public Consumer<Message<MessageResponse>> eventReceived() {
        return message -> {
            log.info("Message eventReceived: {}", message.getPayload());
            MessageResponse msg = message.getPayload();
            setResponse(msg);
        };
    }

    @Bean
    public Consumer<Message<MessageWithJsonIgnoreProperties>> eventReceivedJsonIgnore() {
        return message -> {
            log.info("Message eventReceivedJsonIgnore: {}", message.getPayload());
            MessageWithJsonIgnoreProperties msg = message.getPayload();
            setResponse(msg);
        };
    }

    public void setResponse(Object o) {
        log.info("msg = {}", o);
        this.response = o;
    }

    public Object getLastResponse() {
        return response;
    }
}
