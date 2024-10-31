package com.t1.profile.service;

import com.t1.profile.dto.UserDto;
import com.t1.profile.dto.UserHardSkillDto;
import com.t1.profile.dto.UserHardSkillsCategorizedDto;
import com.t1.profile.exeption.ResourceNotFoundException;
import com.t1.profile.mapper.HardSkillMapper;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserHardSkillServiceImplTest {

    @InjectMocks
    private UserHardSkillServiceImpl userHardSkillService;

    @Mock
    private HardSkillRepo hardSkillRepo;

    @Mock
    private UserHardSkillRepo userHardSkillRepo;

    @Mock
    private ProfessionRepo professionRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserHardSkillMapper userHardSkillMapper;

    @Mock
    private HardSkillMapper hardSkillMapper;

    @Mock
    private UserMapper userMapper;

    private User user;
    private HardSkill hardSkill;
    private UserHardSkill userHardSkill;
    private UserHardSkillDto userHardSkillDto;
    private UserDto userDto;
    private List<UserHardSkill> userHardSkills;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setUserHardSkills(new HashSet<>());
        user.setProfession(new Profession());

        hardSkill = new HardSkill();
        hardSkill.setId(1);
        hardSkill.setName("Java");

        userHardSkill = new UserHardSkill();
        userHardSkill.setId(1);
        userHardSkill.setUser(user);
        userHardSkill.setHardSkill(hardSkill);
        userHardSkill.setRating(5);

        userHardSkillDto = new UserHardSkillDto();
        userHardSkillDto.setId(1);
        userHardSkillDto.setHardSkill(hardSkillMapper.toDto(hardSkill));
        userHardSkillDto.setRating(5);

        userDto = new UserDto();
        userDto.setId(1);
        UserHardSkillDto userHardSkillDto = new UserHardSkillDto();
        userDto.setUserHardSkills(new HashSet<>(Collections.singletonList(userHardSkillDto)));

        userHardSkills = new ArrayList<>();
        userHardSkills.add(userHardSkill);
    }

    @Test
    public void getHardSkillsByUser_shouldReturnSetOfUserHardSkillDto() {
        user.setUserHardSkills(new HashSet<>(userHardSkills));
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userHardSkillMapper.toDto(any(UserHardSkill.class))).thenReturn(userHardSkillDto);

        Set<UserHardSkillDto> result = userHardSkillService.getHardSkillsByUser(1);

        assertEquals(1, result.size());
        assertTrue(result.contains(userHardSkillDto));
        verify(userRepo, times(1)).findById(1);
    }

    @Test
    public void updateHardSkillRating_shouldReturnUpdatedUserHardSkillDto1Id() {
        // Настройка мока
        when(userHardSkillRepo.findById(1)).thenReturn(Optional.of(userHardSkill));
        when(userHardSkillMapper.toDto(userHardSkill)).thenReturn(userHardSkillDto);

        // Вызов тестируемого метода
        userHardSkillService.updateHardSkillRating(1, 10);

        //userHardSkillDto.setRating(10);

        // Проверка результатов
        assertEquals(10, userHardSkill.getRating());
        verify(userHardSkillRepo, times(1)).save(userHardSkill);

    }

    @Test
    public void updateHardSkillRating_shouldThrowResourceNotFoundException() {
        when(userHardSkillRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userHardSkillService.updateHardSkillRating(1, 10));
    }

    @Test
    public void removeHardSkillFromUser_shouldRemoveHardSkill1Id() {
        // Настройка мока
        when(userHardSkillRepo.findById(1)).thenReturn(Optional.of(userHardSkill));

        User user = new User();
        user.setId(1);
        user.getUserHardSkills().add(userHardSkill);

        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        userHardSkillService.removeHardSkillFromUser(1);

        verify(userHardSkillRepo, times(1)).delete(userHardSkill);
        assertFalse(user.getUserHardSkills().contains(userHardSkill));
    }

    @Test
    public void removeHardSkillFromUser_shouldThrowIllegalArgumentException() {
        when(userHardSkillRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userHardSkillService.removeHardSkillFromUser(1));
    }

    @Test
    public void removeHardSkillFromUser_shouldThrowResourceNotFoundExceptionForUser() {
        // Настройка мока
        when(userHardSkillRepo.findById(1)).thenReturn(Optional.of(userHardSkill));
        when(userRepo.findById(1)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> userHardSkillService.removeHardSkillFromUser(1));
    }

    @Test
    public void getHardSkillsByUser_shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userHardSkillService.getHardSkillsByUser(1));

        assertEquals("Пользователь не найден с id 1", exception.getMessage());
    }

    @Test
    public void addHardSkillToUser_shouldReturnUserDto() {
        user.setUserHardSkills(new HashSet<>());

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(hardSkillRepo.findById(1)).thenReturn(Optional.of(hardSkill));
        when(userHardSkillRepo.findByUserId(1)).thenReturn(Collections.emptyList());
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userHardSkillService.addHardSkillToUser(1, 1, 5);

        assertEquals(userDto.getId(), result.getId());
        verify(userHardSkillRepo, times(1)).save(any(UserHardSkill.class));
    }

    @Test
    public void addHardSkillToUser_shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userHardSkillService.addHardSkillToUser(1, 1, 5));

        assertEquals("Пользователь не найден с id 1", exception.getMessage());
    }

    @Test
    public void addHardSkillToUser_shouldThrowResourceNotFoundException_whenHardSkillNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(hardSkillRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userHardSkillService.addHardSkillToUser(1, 1, 5));

        assertEquals("Хардскилл не найден с id 1", exception.getMessage());
    }

    @Test
    public void addHardSkillToUser_shouldThrowIllegalArgumentException_whenHardSkillAlreadyAssociated() {
        user.setUserHardSkills(new HashSet<>(userHardSkills));
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(hardSkillRepo.findById(1)).thenReturn(Optional.of(hardSkill));
        when(userHardSkillRepo.findByUserId(1)).thenReturn(userHardSkills);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userHardSkillService.addHardSkillToUser(1, 1, 5));

        assertEquals("Хардскилл уже ассоциирован с пользователем.", exception.getMessage());
    }

    @Test
    public void updateHardSkillRating_shouldReturnUpdatedUserHardSkillDto() {
        userHardSkill.setRating(5);

        when(userHardSkillRepo.findByUserId(1)).thenReturn(Collections.singletonList(userHardSkill));
        when(userHardSkillMapper.toDto(userHardSkill)).thenReturn(userHardSkillDto);

        userHardSkillService.updateHardSkillRating(1, 1, 10);

        assertEquals(10, userHardSkill.getRating());
        verify(userHardSkillRepo, times(1)).save(userHardSkill);

    }

    @Test
    public void updateHardSkillRating_shouldThrowResourceNotFoundException_whenHardSkillNotAssociated() {
        when(userHardSkillRepo.findByUserId(1)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userHardSkillService.updateHardSkillRating(1, 1, 10));

        assertEquals("Хардскилл не ассоциирован с пользователем.", exception.getMessage());
    }

    @Test
    public void removeHardSkillFromUser_shouldRemoveHardSkill() {
        user.setUserHardSkills(new HashSet<>(userHardSkills));
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userHardSkillRepo.findByUserId(1)).thenReturn(userHardSkills);

        userHardSkillService.removeHardSkillFromUser(1, 1);

        assertFalse(user.getUserHardSkills().contains(userHardSkill));
        verify(userHardSkillRepo, times(1)).delete(userHardSkill);
    }

    @Test
    public void removeHardSkillFromUser_shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userHardSkillService.removeHardSkillFromUser(1, 1));

        assertEquals("Пользователь не найден с id 1", exception.getMessage());
    }

    @Test
    public void removeHardSkillFromUser_shouldThrowIllegalArgumentException_whenHardSkillNotAssociated() {
        user.setUserHardSkills(new HashSet<>());
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userHardSkillRepo.findByUserId(1)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userHardSkillService.removeHardSkillFromUser(1, 1));

        assertEquals("Хардскилл не ассоциирован с пользователем.", exception.getMessage());
    }

    @Test
    public void getUserAndProfessionHardSkills_shouldReturnUserHardSkillsCategorizedDto() {
        Profession profession = new Profession();
        profession.setId(1);
        profession.setMainHardSkills(new HashSet<>(Collections.singletonList(hardSkill)));

        user.setProfession(profession);
        user.setUserHardSkills(new HashSet<>(userHardSkills));

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(professionRepo.findById(1)).thenReturn(Optional.of(profession));
        when(hardSkillRepo.findByProfessionId(1)).thenReturn(Collections.singletonList(hardSkill));
        when(userHardSkillRepo.findByUserId(1)).thenReturn(userHardSkills);

        UserHardSkillsCategorizedDto result = userHardSkillService.getUserAndProfessionHardSkills(1, 1);

        assertNotNull(result);
        assertEquals(1, result.getCommonHardSkills().size());
        assertEquals(0, result.getRemainingUserHardSkills().size());
    }

    @Test
    public void getUserAndProfessionHardSkills_shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userHardSkillService.getUserAndProfessionHardSkills(1, 1));

        assertEquals("Пользователь не найден с id 1", exception.getMessage());
    }

    @Test
    public void getUserAndProfessionHardSkills_shouldThrowResourceNotFoundException_whenProfessionNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.of(new User()));

        when(professionRepo.findById(999)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userHardSkillService.getUserAndProfessionHardSkills(1, 999));

        assertEquals("Профессия не найдена для пользователя с id 1", exception.getMessage());
    }

    @Test
    public void getUserAndProfessionHardSkills_shouldReturnCategorizedHardSkills_whenNoCommonSkills() {
        Profession profession = new Profession();
        profession.setId(1);

        // Установка профессии у пользователя
        user.setProfession(profession);
        user.setUserHardSkills(new HashSet<>(Collections.singletonList(userHardSkill)));

        // Создание хардскилла, который отличается от того, который есть у пользователя
        HardSkill differentSkill = new HardSkill();
        differentSkill.setId(2);
        differentSkill.setName("Python");

        // Настройка поведения моков
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        // Здесь мы настраиваем мок для professionRepo, чтобы он возвращал профессию
        when(professionRepo.findById(1)).thenReturn(Optional.of(profession));
        when(hardSkillRepo.findByProfessionId(1)).thenReturn(Collections.singletonList(differentSkill));
        when(userHardSkillRepo.findByUserId(1)).thenReturn(Collections.singletonList(userHardSkill));

        // Вызов тестируемого метода
        UserHardSkillsCategorizedDto result = userHardSkillService.getUserAndProfessionHardSkills(1, 1);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(0, result.getCommonHardSkills().size());
        assertEquals(1, result.getRemainingUserHardSkills().size());
    }

}
