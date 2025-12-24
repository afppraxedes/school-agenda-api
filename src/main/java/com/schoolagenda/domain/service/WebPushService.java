package com.schoolagenda.domain.service;

import java.util.List;

/**
 * Serviço para envio de notificações Web Push
 */
public interface WebPushService {

    /**
     * Envia uma notificação push para uma subscription em formato JSON string
     */
    void sendNotification(String subscriptionJson, String title, String message);

    /**
     * Envia uma notificação push para múltiplos usuários
     */
    void sendBulkNotification(List<String> subscriptionJsons, String title, String message);

    /**
     * Valida se uma subscription é válida
     */
    boolean isValidSubscription(String subscriptionJson);

    /**
     * Obtém a chave pública VAPID em formato string
     */
    String getVapidPublicKey();
}