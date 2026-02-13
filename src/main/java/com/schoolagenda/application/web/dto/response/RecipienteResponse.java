package com.schoolagenda.application.web.dto.response;

/**
 * DTO para popular o dropdown de destinatários no envio de mensagens.
 */
public record RecipienteResponse (
    Long id,      // ID do usuário
    String name,  // Nome completo para exibição
    String role   // Perfil (STUDENT ou RESPONSIBLE) para rotulagem na UI
) {}
