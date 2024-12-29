package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Autowired
    public UserService(@Qualifier("UserRepositoryImpl") UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.mapper = userMapper;
    }

    public UserDto create(UserDto userDto) {
        User user = mapper.toUser(userDto);
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Почта пользователя не может быть пустой.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Имя пользователя не может быть пустым.");
        }
        Long idFromDbByEmail = userRepository.getUserIdByEmail(user.getEmail());
        if (idFromDbByEmail != null) {
            throw new AlreadyExistsException("Пользователь с почтой = " + user.getEmail() + " уже существует.");
        }
        return mapper.toUserDto(userRepository.create(user));
    }

    public UserDto update(UserDto userDto, Long id) {
        userDto.setId(id);
        if (userDto.getName() == null) {
            userDto.setName(userRepository.getUserById(id).getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(userRepository.getUserById(id).getEmail());
        }
        User user = mapper.toUser(userDto);
        if (userRepository.getUserById(user.getId()) == null) {
            throw new NotFoundException("Пользователь с ID = " + user.getId() + " не найден.");
        }
        if (user.getId() == null) {
            throw new ValidationException("ID пользователя не может быть пустым.");
        }
        final Long idFromDbByEmail = userRepository.getUserIdByEmail(user.getEmail());
        if (idFromDbByEmail != null && !user.getId().equals(idFromDbByEmail)) {
            throw new AlreadyExistsException("Пользователь с почтой = " + user.getEmail() + " уже существует.");
        }
        User updateUser = userRepository.update(user);
        return mapper.toUserDto(updateUser);
    }

    public UserDto delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть пустым.");
        }
        if (!userRepository.isExistUserInDb(userId)) {
            throw new NotFoundException("Пользователь с ID = " + userId + " не найден.");
        }
        return mapper.toUserDto(userRepository.delete(userId));
    }

    public List<UserDto> getUsers() {
        return userRepository.getUsers().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID = " + id + " не найден.");
        }
        return mapper.toUserDto(userRepository.getUserById(id));
    }
}