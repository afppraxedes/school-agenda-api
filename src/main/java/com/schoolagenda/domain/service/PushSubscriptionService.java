package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.PushSubscriptionRequest;
import com.schoolagenda.domain.model.User;

public interface PushSubscriptionService {
    void subscribe(User user, PushSubscriptionRequest subscriptionDTO);
    // Remove a inscrição de push notifications para um usuário e endpoint específicos
    void unsubscribe(User user, String endpoint);
    // Remove todas as inscrições de push notifications de um usuário
     void unsubscribeAll(User user);
    // Remove subscription por endpoint (sem verificar usuário - para admin)
    void unsubscribeByEndpoint(String endpoint);
}
