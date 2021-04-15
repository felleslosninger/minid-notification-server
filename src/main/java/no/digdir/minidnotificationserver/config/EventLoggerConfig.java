package no.digdir.minidnotificationserver.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.difi.resilience.spring.ResilientJmsTemplate;
import no.idporten.log.event.EventLogger;
import no.idporten.log.event.EventLoggerImpl;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
@ConfigurationProperties(prefix = "log.event")
@Data
@Slf4j
public class EventLoggerConfig {

    private String jmsUrl;
    private String jmsQueue;

    @Bean
    public EventLogger eventLogger() {
        return new EventLoggerImpl(eventQueueJmsTemplate());
    }

    @Bean
    public JmsTemplate eventQueueJmsTemplate() {
        JmsTemplate jmsTemplate = new ResilientJmsTemplate("SERVICEID_minid-notification-server");
        jmsTemplate.setConnectionFactory(eventQueueJmsConnectionFactory());
        jmsTemplate.setDefaultDestination(new ActiveMQQueue(jmsQueue));
        return jmsTemplate;
    }

    @Bean
    public ConnectionFactory eventQueueJmsConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(jmsUrl);
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
        cachingConnectionFactory.setSessionCacheSize(10);
        return cachingConnectionFactory;
    }

}
