package com.t1.profile.mapper;

import com.t1.profile.dto.UserHardSkillDto;
import com.t1.profile.model.UserHardSkill;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserHardSkillMapper extends BaseMapper<UserHardSkillDto, UserHardSkill> {

    @Override
    UserHardSkill toEntity(UserHardSkillDto dto);

    @Override
    UserHardSkillDto toDto(UserHardSkill entity);

    @Override
    List<UserHardSkillDto> toDtoList(List<UserHardSkill> entities);

    @Override
    List<UserHardSkill> toEntityList(List<UserHardSkillDto> dtos);

}
