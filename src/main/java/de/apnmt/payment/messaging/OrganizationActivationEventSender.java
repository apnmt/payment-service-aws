package de.apnmt.payment.messaging;

import de.apnmt.common.event.ApnmtEvent;
import de.apnmt.common.event.value.OrganizationActivationEventDTO;
import de.apnmt.common.sender.ApnmtEventSender;
import io.awspring.cloud.messaging.core.NotificationMessagingTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationActivationEventSender implements ApnmtEventSender<OrganizationActivationEventDTO> {

    private final NotificationMessagingTemplate notificationMessagingTemplate;

    public OrganizationActivationEventSender(NotificationMessagingTemplate notificationMessagingTemplate) {
        this.notificationMessagingTemplate = notificationMessagingTemplate;
    }

    @Override
    public void send(String topic, ApnmtEvent<OrganizationActivationEventDTO> event) {
        this.notificationMessagingTemplate.convertAndSend(topic, event);
    }

}
