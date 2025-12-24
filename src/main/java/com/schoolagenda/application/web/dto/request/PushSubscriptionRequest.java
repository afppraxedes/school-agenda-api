package com.schoolagenda.application.web.dto.request;

//public class PushSubscriptionDTO {
//    private String subscription;
//
//    public PushSubscriptionDTO() {}
//
//    public PushSubscriptionDTO(String subscription) {
//        this.subscription = subscription;
//    }
//
//    public String getSubscription() { return subscription; }
//    public void setSubscription(String subscription) { this.subscription = subscription; }
//}

public class PushSubscriptionRequest {
    private String endpoint;
    private KeysDTO keys;

    // Getters e Setters
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public KeysDTO getKeys() { return keys; }
    public void setKeys(KeysDTO keys) { this.keys = keys; }

    public static class KeysDTO {
        private String p256dh;
        private String auth;

        // Getters e Setters
        public String getP256dh() { return p256dh; }
        public void setP256dh(String p256dh) { this.p256dh = p256dh; }

        public String getAuth() { return auth; }
        public void setAuth(String auth) { this.auth = auth; }
    }
}