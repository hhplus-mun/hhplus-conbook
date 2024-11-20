package io.hhplus.conbook.testcontainers;

import io.hhplus.conbook.testcontainers.kafka.KafkaTestConsumer;
import io.hhplus.conbook.testcontainers.kafka.KafkaTestProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaContainerBuilderTest extends KafkaContainerBuilder {

    @Autowired
    KafkaTestProducer testProducer;
    @Autowired
    KafkaTestConsumer testConsumer;

    @Test
    @DisplayName("[정상] Kafka 연동 테스트")
    void kafkaConnectionTest() throws InterruptedException {
        //given & when
        String message = "";

        for (int i = 0; i < 5; i++) {
            message = "[MESSAGE] produced at " + i;
            testProducer.produce(message);
        }

        // then
        Thread.sleep(10000); // 10초 대기 (consumer의 메시지 처리시간을 기다려준다.)
        String lastReceivedMessage = testConsumer.getReceivedMessage();

        assertThat(lastReceivedMessage).isEqualTo(message);
    }
}