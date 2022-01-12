package de.apnmt.payment.messaging;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import de.apnmt.aws.common.test.AbstractEventSenderIT;
import de.apnmt.aws.common.test.SqsMessage;
import de.apnmt.common.ApnmtTestUtil;
import de.apnmt.common.TopicConstants;
import de.apnmt.common.event.ApnmtEvent;
import de.apnmt.common.event.value.OrganizationActivationEventDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
class OrganizationActivationEventSenderIT extends AbstractEventSenderIT {

    @Autowired
    private OrganizationActivationEventSender organizationActivatedEventSenderIT;

    @Test
    void eventSenderTest() throws InterruptedException, JsonProcessingException {
        ApnmtEvent<OrganizationActivationEventDTO> event = ApnmtTestUtil.createOrganizationActivationEvent();
        this.organizationActivatedEventSenderIT.send(getTopic(), event);

        Thread.sleep(3000);
        await().pollInterval(Duration.ofMillis(500)).atMost(Duration.ofMillis(10000)).until(assertEvent(event));
    }

    private Callable<Boolean> assertEvent(ApnmtEvent<OrganizationActivationEventDTO> event) throws JsonProcessingException {
        List<Message> events = sqsAsync.receiveMessage("/000000000000/" + getQueue()).getMessages();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isNotNull();
        SqsMessage sqsMessage = this.objectMapper.readValue(events.get(0).getBody(), SqsMessage.class);
        TypeReference<ApnmtEvent<OrganizationActivationEventDTO>> eventType = new TypeReference<>() {
        };
        ApnmtEvent<OrganizationActivationEventDTO> eventResult = this.objectMapper.readValue(sqsMessage.getMessage(), eventType);
        assertThat(eventResult).isEqualTo(event);
        return () -> true;
    }

    @Override
    protected String getTopic() {
        return TopicConstants.ORGANIZATION_ACTIVATION_CHANGED_TOPIC;
    }

    @Override
    protected String getQueue() {
        return "organization-activation-changed-queue";
    }

}
