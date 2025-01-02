package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен POST-request: '/users' добавить пользователя.");
        return userService.create(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@Positive @PathVariable long userId) {
        log.info("Получен GET-request: '/users' получить пользователя с ID = {}", userId);
        return userService.findUserById(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Получен GET-request: '/users' получить всех пользователей");
        return userService.findAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto save(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Получен PATCH-request: '/users' обновить пользователя с ID = {}", userId);
        return userService.save(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Получен DELETE-request: '/users' удалить пользователя с ID = {}", userId);
        userService.delete(userId);
    }
}