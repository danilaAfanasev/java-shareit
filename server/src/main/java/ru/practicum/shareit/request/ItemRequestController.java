package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto findRequestById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                          @PathVariable Long requestId) {
        return requestService.findById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findRequests(@RequestHeader(OWNER_ID_HEADER) Long userId) {
        return requestService.findRequests(userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(OWNER_ID_HEADER) Long userId) {
        return requestService.findUserRequests(userId);
    }
}