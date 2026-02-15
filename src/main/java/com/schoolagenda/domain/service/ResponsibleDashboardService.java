package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.StudentSummaryDTO;
import com.schoolagenda.application.web.dto.response.ResponsibleDashboardResponse;
import com.schoolagenda.domain.model.ResponsibleStudent;
import com.schoolagenda.domain.repository.MessageRepository;
import com.schoolagenda.domain.repository.ResponsibleStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Transformar para interface e implementar no pacote impl, seguindo o padrão dos outros serviços.
@Service
@RequiredArgsConstructor
public class ResponsibleDashboardService {

    private final ResponsibleStudentRepository responsibleStudentRepository;
    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public ResponsibleDashboardResponse getResponsibleDashboard(Long responsibleUserId) {
        // 1. Busca os vínculos com Join Fetch
        List<ResponsibleStudent> relationships = responsibleStudentRepository
                .findAllByResponsibleUserId(responsibleUserId);

        // 2. Mapeia para os resumos dos filhos
        List<StudentSummaryDTO> children = relationships.stream()
                .map(rs -> StudentSummaryDTO.builder()
                        .studentId(rs.getStudent().getId())
                        .name(rs.getStudent().getFullName())
                        .className(rs.getStudent().getClassName())
                        // Garante que o valor 8.1666... vire 8.17 para o Aluno 23
                        .globalAverage(rs.getStudent().getGlobalAverage() != null ?
                                rs.getStudent().getGlobalAverage().setScale(2, RoundingMode.HALF_UP) : null)
                        .build())
                .collect(Collectors.toList());

        // 3. Busca contagem de mensagens não lidas para o responsável
        long unreadCount = messageRepository.countUnreadMessagesByRecipientId(responsibleUserId);

        String responsibleName = relationships.isEmpty() ? "" : relationships.get(0).getResponsible().getName();

        return ResponsibleDashboardResponse.builder()
                .responsibleName(responsibleName)
                .unreadMessagesCount(unreadCount)
                .children(children)
                .build();
    }

}
