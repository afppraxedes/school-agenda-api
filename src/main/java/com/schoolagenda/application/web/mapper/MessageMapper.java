package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.response.MessageResponse;
import com.schoolagenda.domain.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "sender.name", target = "senderName")
    @Mapping(source = "recipient.id", target = "recipientId")
    @Mapping(source = "recipient.name", target = "recipientName")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.name", target = "studentName")
    MessageResponse toResponse(Message message);
}
