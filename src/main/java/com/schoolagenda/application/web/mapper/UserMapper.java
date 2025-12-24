package com.schoolagenda.application.web.mapper;


import com.schoolagenda.application.web.dto.request.CreateUserRequest;
import com.schoolagenda.application.web.dto.request.UpdateUserRequest;
import com.schoolagenda.application.web.dto.response.UserResponse;
import com.schoolagenda.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;


@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = IGNORE,
        // Para verificar sempre se há valores nulos
        nullValueCheckStrategy = ALWAYS,
        // Para remover o "warning": Unmapped target properties: "withId, withName, withEmail, withPassword, withProfiles".
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    // Esta anotação é para quando temos atributos direferentes. Como temos
    // para o tipo "Set<ProfileEnum>" a propriedade "profiles" em "hd-commons-lib",
    // dará um "warning" ao fazer o "build", pois na classe "User" o atributo está
    // como "profile"!
    // NOTA: a melhor forma é deixar os atributos com o mesmo "nome/descrição".
    // ISTO É APENAS UM EXEMPLO DE UTILIZAÇÃO DO "mapping" do "MapStruct"!
    // @Mapping(target = "profiles", source = "profile")
    UserResponse fromEntity(final User entity);

    // Para ignorar a propriedade "id"
    @Mapping(target = "id", ignore = true)
    User fromRequest(CreateUserRequest createUserRequest);

    // Para ignorar a propriedade "id"
    @Mapping(target = "id", ignore = true)
    // A anotação "@MappingTarge" informa qual o "alvo a ser mapeado". Nesse caso o "entity".
    // Ou seja, as informações do "updateUserRequest" serão repassadas para "entity"!
    User update(UpdateUserRequest updateUserRequest, @MappingTarget User entity);
}
