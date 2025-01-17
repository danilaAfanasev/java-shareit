package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(OWNER_ID_HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен POST-request: '/items' добавить вещь владельца с ID = {}", userId);
        return itemService.create(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(OWNER_ID_HEADER) Long userId, @PathVariable Long itemId) {
        log.info("Получен GET-request: '/items' получить вещь с ID = {}", itemId);
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Получен GET-request: '/items' получить все вещи владельца с ID = {}", userId);
        return itemService.findAllUsersItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto save(@RequestHeader(OWNER_ID_HEADER) Long userId, @PathVariable long itemId,
                        @RequestBody ItemDto itemDto) {
        log.info("Получен PATCH-request: '/items' обновить вещь с ID = {}", itemId);
        return itemService.save(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        log.info("Получен DELETE-request: '/items' удалить вещь с ID = {}", itemId);
        itemService.deleteById(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        log.info("Получен GET-request: '/items/search' найти вещь с текстом = {}", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен POST-request: '/items/{itemId}/comment' добавить отзыв");
        return itemService.addComment(itemId, userId, commentDto);
    }
}
