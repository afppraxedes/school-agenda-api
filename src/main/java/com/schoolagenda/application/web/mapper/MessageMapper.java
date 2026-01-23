package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.MessageRequest;
import com.schoolagenda.application.web.dto.response.MessageResponse;
import com.schoolagenda.domain.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "sender.name", target = "senderName")
    @Mapping(source = "recipient.id", target = "recipientId")
    @Mapping(source = "recipient.name", target = "recipientName")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.fullName", target = "studentName")
    MessageResponse toResponse(Message message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "recipient", ignore = true)
    @Mapping(target = "student", ignore = true)
    Message toEntity(MessageRequest request);
}
