package com.schoolagenda.application.web.dto;

public class PushSubscriptionDTO {
    private String subscription;

    public PushSubscriptionDTO() {}

    public PushSubscriptionDTO(String subscription) {
        this.subscription = subscription;
    }

    public String getSubscription() { return subscription; }
    public void setSubscription(String subscription) { this.subscription = subscription; }
}