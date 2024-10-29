package com.t1.profile.service;

import com.t1.profile.dto.UserDto;
import com.t1.profile.dto.UserHardSkillDto;
import com.t1.profile.dto.UserHardSkillsCategorizedDto;

import java.util.Set;

public interface UserHardSkillService {

    Set<UserHardSkillDto> getHardSkillsByUser(Integer userId);
    UserDto addHardSkillToUser(Integer userId, Integer hardSkillId, Integer rating);
    UserHardSkillDto updateHardSkillRating(Integer userId, Integer hardSkillId, Integer newRating);
    UserHardSkillDto updateHardSkillRating(Integer userHardSkillId, Integer newRating);
    void removeHardSkillFromUser(Integer userId, Integer hardSkillId);
    void removeHardSkillFromUser(Integer userHardSkillId);
    UserHardSkillsCategorizedDto getUserAndProfessionHardSkills(Integer userId);
    UserHardSkillsCategorizedDto getUserAndProfessionHardSkills(Integer userId, Integer professionId);

}
