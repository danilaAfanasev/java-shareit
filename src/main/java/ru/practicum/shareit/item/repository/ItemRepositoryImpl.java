package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Repository
@Qualifier("ItemRepositoryImpl")
public class ItemRepositoryImpl implements ItemRepository {

    private Map<Long, Item> items;
    private Long currentId;

    public ItemRepositoryImpl() {
        currentId = 0L;
        items = new HashMap<>();
    }

    @Override
    public Item create(Item item) {
        item.setId(++currentId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item delete(Long itemId) {
        return items.remove(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        return new ArrayList<>(items
                .values()
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(toList()));
    }

    @Override
    public List<Item> getItemsBySearchQuery(String text) {
        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(toList());
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public void deleteItemsByOwner(Long ownerId) {
        items.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .map(Item::getId)
                .forEach(id -> items.remove(id));
    }
}