package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestDto findById(Long userId, Long requestId);

    List<ItemRequestDto> findRequests(Long userId);

    List<ItemRequestDto> findUserRequests(Long userId);
}
