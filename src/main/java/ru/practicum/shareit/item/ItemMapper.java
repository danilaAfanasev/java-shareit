package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
@NoArgsConstructor
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId() != null ? item.getRequestId() : null)
                .build();
    }

    public Item toItem(ItemDto itemDto, Long ownerId) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .ownerId(ownerId)
                .requestId(itemDto.getRequestId() != null ? itemDto.getRequestId() : null)
                .build();
    }
}
