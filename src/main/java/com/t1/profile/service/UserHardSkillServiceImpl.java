package com.t1.profile.service;

import com.t1.profile.dto.UserDto;
import com.t1.profile.dto.UserHardSkillDto;
import com.t1.profile.dto.UserHardSkillsCategorizedDto;
import com.t1.profile.exeption.ResourceNotFoundException;
import com.t1.profile.mapper.UserHardSkillMapper;
import com.t1.profile.mapper.UserMapper;
import com.t1.profile.model.HardSkill;
import com.t1.profile.model.Profession;
import com.t1.profile.model.User;
import com.t1.profile.model.UserHardSkill;
import com.t1.profile.repository.HardSkillRepo;
import com.t1.profile.repository.ProfessionRepo;
import com.t1.profile.repository.UserHardSkillRepo;
import com.t1.profile.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserHardSkillServiceImpl implements UserHardSkillService {

    @Autowired
    private HardSkillRepo hardSkillRepo;

    @Autowired
    private UserHardSkillRepo userHardSkillRepo;

    @Autowired
    private ProfessionRepo professionRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserHardSkillMapper userHardSkillMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Set<UserHardSkillDto> getHardSkillsByUser(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id " + userId));
        return user.getUserHardSkills().stream()
                .map(userHardSkillMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public UserDto addHardSkillToUser(Integer userId, Integer hardSkillId, Integer rating) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id " + userId));

        HardSkill hardSkill = hardSkillRepo.findById(hardSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("Хардскилл не найден с id " + hardSkillId));

        List<UserHardSkill> existingUserHardSkills = userHardSkillRepo.findByUserId(userId);
        for (UserHardSkill uhs : existingUserHardSkills) {
            if (uhs.getHardSkill().getId().equals(hardSkillId)) {
                throw new IllegalArgumentException("Хардскилл уже ассоциирован с пользователем.");
            }
        }
        UserHardSkill userHardSkill = new UserHardSkill();
        userHardSkill.setUser(user);
        userHardSkill.setHardSkill(hardSkill);
        userHardSkill.setRating(rating);

        userHardSkillRepo.save(userHardSkill);
        return userMapper.toDto(user);
    }


    @Override
    @Transactional
    public UserHardSkillDto updateHardSkillRating(Integer userId, Integer hardSkillId, Integer newRating) {
        UserHardSkill userHardSkill = userHardSkillRepo.findByUserId(userId).stream()
                .filter(uhs -> uhs.getHardSkill().getId().equals(hardSkillId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Хардскилл не ассоциирован с пользователем."));

        userHardSkill.setRating(newRating);
        userHardSkillRepo.save(userHardSkill);

        return userHardSkillMapper.toDto(userHardSkill);
    }

    @Override
    public UserHardSkillDto updateHardSkillRating(Integer userHardSkillId, Integer newRating) {
        UserHardSkill userHardSkill = userHardSkillRepo.findById(userHardSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("Хардскилл не найден с id " + userHardSkillId));

        userHardSkill.setRating(newRating);
        userHardSkillRepo.save(userHardSkill);

        return userHardSkillMapper.toDto(userHardSkill);
    }

    @Override
    public void removeHardSkillFromUser(Integer userId, Integer hardSkillId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id " + userId));

        UserHardSkill userHardSkill = userHardSkillRepo.findByUserId(userId).stream()
                .filter(uhs -> uhs.getHardSkill().getId().equals(hardSkillId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Хардскилл не ассоциирован с пользователем."));

        userHardSkillRepo.delete(userHardSkill);
        user.getUserHardSkills().remove(userHardSkill);
    }

    @Override
    public void removeHardSkillFromUser(Integer userHardSkillId) {
        UserHardSkill userHardSkill = userHardSkillRepo.findById(userHardSkillId)
                .orElseThrow(() -> new IllegalArgumentException("Хардскилл не найден с id " + userHardSkillId));

        userHardSkillRepo.delete(userHardSkill);

        User user = userRepo.findById(userHardSkill.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Пользователь не найден с id " + userHardSkill.getUser().getId())
                );

        user.getUserHardSkills().remove(userHardSkill);
    }

    @Override
    public UserHardSkillsCategorizedDto getUserAndProfessionHardSkills(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id " + userId));

        Profession profession = user.getProfession();
        if (profession == null) {
            throw new ResourceNotFoundException("Профессия не найдена для пользователя с id " + userId);
        }

        Integer professionId = profession.getId();

        List<UserHardSkill> userHardSkills = userHardSkillRepo.findByUserId(userId);
        List<HardSkill> professionHardSkills = hardSkillRepo.findByProfessionId(professionId);

        Set<Integer> professionSkillIds = professionHardSkills.stream()
                .map(HardSkill::getId)
                .collect(Collectors.toSet());

        List<UserHardSkill> commonHardSkills = new ArrayList<>();
        List<UserHardSkill> remainingUserHardSkills = new ArrayList<>(userHardSkills);

        for (UserHardSkill userHardSkill : userHardSkills) {
            if (professionSkillIds.contains(userHardSkill.getHardSkill().getId())) {
                commonHardSkills.add(userHardSkill);
                remainingUserHardSkills.remove(userHardSkill);
            }
        }

        return new UserHardSkillsCategorizedDto(commonHardSkills, remainingUserHardSkills);
    }

    @Override
    public UserHardSkillsCategorizedDto getUserAndProfessionHardSkills(Integer userId, Integer professionId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id " + userId));

        Profession profession = professionRepo.findById(professionId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Профессия не найдена для пользователя с id " + userId));

        List<UserHardSkill> userHardSkills = userHardSkillRepo.findByUserId(userId);
        List<HardSkill> professionHardSkills = hardSkillRepo.findByProfessionId(professionId);

        Set<Integer> professionSkillIds = professionHardSkills.stream()
                .map(HardSkill::getId)
                .collect(Collectors.toSet());

        List<UserHardSkill> commonHardSkills = new ArrayList<>();
        List<UserHardSkill> remainingUserHardSkills = new ArrayList<>(userHardSkills);

        for (UserHardSkill userHardSkill : userHardSkills) {
            if (professionSkillIds.contains(userHardSkill.getHardSkill().getId())) {
                commonHardSkills.add(userHardSkill);
                remainingUserHardSkills.remove(userHardSkill);
            }
        }

        return new UserHardSkillsCategorizedDto(commonHardSkills, remainingUserHardSkills);
    }

}
