package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestServiceImpl requestService;
    private final User user = new User(1L, "User", "user@email.com");
    private final UserDto userDto = new UserDto(1L, "User", "user@email.com");

    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("description")
            .requester(user)
            .items(new ArrayList<>())
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .requester(userDto)
            .items(new ArrayList<>())
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("ItemName")
            .description("description")
            .available(true)
            .itemRequest(itemRequest)
            .ownerId(1L)
            .build();
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("ItemName")
            .description("description")
            .available(true)
            .requestId(1L)
            .build();
    private final List<Booking> bookingList = List.of(Booking.builder()
                    .id(1L).item(item).booker(user)
                    .start(LocalDateTime.now().minusHours(2L))
                    .end(LocalDateTime.now().minusHours(1L))
                    .status(BookingStatus.WAITING).build(),
            Booking.builder()
                    .id(2L).item(item).booker(user)
                    .start(LocalDateTime.now().plusHours(1L))
                    .end(LocalDateTime.now().plusHours(2L))
                    .status(BookingStatus.WAITING).build());

    private final Comment comment = Comment.builder().id(1L).text("Text").item(item).author(user).build();
    private final CommentDto commentDto = CommentDto.builder().id(1L).text("Text").item(itemDto).authorName("User").build();

    @Test
    void createItemWhenAllIsValidThenReturnedExpectedItem() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);

        Mockito.when(requestService.findById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        Mockito.when(itemRepository.save(any()))
                .thenReturn(item);

        assertEquals(itemService.create(1L, itemDto), itemDto);
    }

    @Test
    void createItemWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format(String.format("Вещь с ID = %d не найдена.", 100L))));

        Exception e = assertThrows(NotFoundException.class,
                () -> itemService.create(100L, itemDto));

        assertEquals(e.getMessage(), String.format("Вещь с ID = %d не найдена.", 100L));
    }

    @Test
    void findByIdWhenParamsIsValidThenReturnedExpectedItem() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(commentRepository.findByItemId(anyLong()))
                .thenReturn(List.of(comment));

        Mockito.when(bookingRepository.findBookingsItem(anyLong()))
                .thenReturn(bookingList);

        ItemDto requestedItemDto = itemService.findItemById(1L, 1L);

        assertEquals(requestedItemDto.getName(), item.getName());
        assertEquals(requestedItemDto.getDescription(), item.getDescription());
        assertEquals(requestedItemDto.getAvailable(), item.getAvailable());
        assertEquals(requestedItemDto.getLastBooking().getId(), 1L);
        assertEquals(requestedItemDto.getLastBooking().getBookerId(), 1L);
        assertEquals(requestedItemDto.getNextBooking().getId(), 2L);
        assertEquals(requestedItemDto.getNextBooking().getBookerId(), 1L);
    }

    @Test
    void findByIdWhenItemNotFoundThenReturnedNotFoundException() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Вещь с ID = %d не найдена.", 100L)));

        Exception e = assertThrows(NotFoundException.class, () -> itemService
                .findItemById(100L, 1L));

        assertEquals(e.getMessage(), String.format("Вещь с ID = %d не найдена.", 100L));
    }

    @Test
    void findAllUserItemsWhenAllParamsIsValidThenReturnedListItems() {
        Mockito.when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(item));

        Mockito.when(bookingRepository.findBookingsItem(anyLong()))
                .thenReturn(bookingList);

        List<ItemDto> userItemsList = itemService.findAllUsersItems(1L);

        assertEquals(userItemsList.get(0).getLastBooking().getId(), 1L);
        assertEquals(userItemsList.get(0).getLastBooking().getBookerId(), 1L);
        assertEquals(userItemsList.get(0).getNextBooking().getId(), 2L);
        assertEquals(userItemsList.get(0).getNextBooking().getBookerId(), 1L);
    }

    @Test
    void updateItemWhenAllParamsIsValidThenReturnedUpdatedItem() {
        ItemDto itemDtoUpdate = ItemDto.builder()
                .id(1L)
                .name("ItemUpdate")
                .description("DescriptionUpdate")
                .available(true)
                .requestId(1L)
                .build();

        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        assertEquals(itemService.save(itemDtoUpdate, 1L, 1L), itemDtoUpdate);
    }

    @Test
    void updateItemWhenUserIsNotOwnerIdThenReturnedOperationAccessException() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Exception e = assertThrows(OperationAccessException.class,
                () -> itemService.save(itemDto, 1L, 2L));

        assertEquals(e.getMessage(), String.format("Пользователь с ID = %d не владелец, обновление недоступно.", 2L));
    }

    @Test
    void updateItemWhenItemIsNotFoundThenReturnedNotFoundException() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception e = assertThrows(NotFoundException.class,
                () -> itemService.save(itemDto, 100L, 1L));

        assertEquals(e.getMessage(), String.format("Вещь с ID = %d не найдена.", 100L));
    }

    @Test
    void searchTestWllParamsIsValidThenReturnedPageableListOfItems() {
        assertThat(itemService.search(""), hasSize(0));
        assertThat(itemService.search(null), hasSize(0));

        Mockito.when(itemRepository.searchAvailableItems(anyString()))
                .thenReturn(List.of(item));

        assertEquals(itemService.search("item"), List.of(itemDto));
    }



    @Test
    void addCommentTest() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(bookingRepository.findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(bookingList);
        Mockito.when(commentRepository.save(any()))
                .thenReturn(comment);
        Mockito.when(commentRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        CommentDto testComment = itemService.addComment(1L, 1L, commentDto);

        assertEquals(testComment.getId(), commentDto.getId());
        assertEquals(testComment.getItem(), commentDto.getItem());
        assertEquals(testComment.getText(), commentDto.getText());
        assertEquals(testComment.getAuthorName(), commentDto.getAuthorName());
    }

    @Test
    void deleteItem() {
        itemService.deleteById(1L);

        Mockito.verify(itemRepository).deleteById(1L);
    }
}