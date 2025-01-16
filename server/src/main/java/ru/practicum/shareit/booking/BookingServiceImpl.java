package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.exception.TimeDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public OutputBookingDto create(InputBookingDto bookingDtoShort, Long bookerId) {
        if (bookingDtoShort.getEnd().isBefore(bookingDtoShort.getStart()) ||
                bookingDtoShort.getEnd().equals(bookingDtoShort.getStart())) {
            throw new TimeDataException(String
                    .format("Некорректное начало времени бронирования = %s  end = %s",
                            bookingDtoShort.getStart(), bookingDtoShort.getEnd()));
        }
        User booker = UserMapper.toUser(userService.findUserById(bookerId));
        Item item = ItemMapper.toItem(itemService.findItemById(bookingDtoShort.getItemId(), bookerId));
        if (itemService.findOwnerId(item.getId()).equals(bookerId)) {
            throw new OperationAccessException("Владелец не может бронировать.");
        }
        if (!item.getAvailable()) {
            throw new NotAvailableException(String.format("Вещь с ID = %d недоступна.", item.getId()));
        } else {
            Booking booking = Booking.builder().start(bookingDtoShort.getStart()).end(bookingDtoShort.getEnd())
                    .item(item).booker(booker).status(BookingStatus.WAITING).build();
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        }
    }

    @Override
    @Transactional
    public OutputBookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с ID = %d не найдено.", bookingId)));
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId))) {
            throw new OperationAccessException(String.format("Пользователь с ID = %d не владелец, нет доступа к бронированию.", userId));
        } else {
            return BookingMapper.toBookingDto(booking);
        }
    }

    @Override
    @Transactional
    public List<OutputBookingDto> findAllBookingsByUser(String state, Long userId) {
        userService.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return BookingMapper.toBookingDto(bookingRepository.findByBookerIdOrderByStartDesc(userId));
            case "CURRENT":
                return BookingMapper.toBookingDto(bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, now, now));
            case "PAST":
                return BookingMapper.toBookingDto(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now));
            case "FUTURE":
                return BookingMapper.toBookingDto(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, now));
            case "WAITING":
                return BookingMapper.toBookingDto(bookingRepository.findByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(userId, now, BookingStatus.WAITING));
            case "REJECTED":
                return BookingMapper.toBookingDto(bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED));
        }
        throw new BadRequestException(String.format("Несуществующий state: %s", state));
    }

    @Override
    @Transactional
    public List<OutputBookingDto> findAllBookingsByOwner(String state, Long ownerId) {
        userService.findUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return BookingMapper.toBookingDto(bookingRepository.findByItemOwnerId(ownerId));
            case "CURRENT":
                return BookingMapper.toBookingDto(bookingRepository.findCurrentBookingsOwner(ownerId, now));
            case "PAST":
                return BookingMapper.toBookingDto(bookingRepository.findPastBookingsOwner(ownerId, now));
            case "FUTURE":
                return BookingMapper.toBookingDto(bookingRepository.findFutureBookingsOwner(ownerId, now));
            case "WAITING":
                return BookingMapper.toBookingDto(bookingRepository.findWaitingBookingsOwner(ownerId, now, BookingStatus.WAITING));
            case "REJECTED":
                return BookingMapper.toBookingDto(bookingRepository.findRejectedBookingsOwner(ownerId, BookingStatus.REJECTED));
        }
        throw new BadRequestException(String.format("Несуществующий state: %s", state));
    }

    @Override
    @Transactional
    public OutputBookingDto approve(long bookingId, long userId, Boolean approve) {
        OutputBookingDto booking = findBookingById(bookingId, userId);
        Long ownerId = itemService.findOwnerId(booking.getItem().getId());
        if (ownerId.equals(userId) && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AlreadyExistsException("Решение бронирования уже сделано.");
        }
        if (!ownerId.equals(userId)) {
            throw new OperationAccessException(String.format("Пользователь с ID = %d не владелец, нет доступа к бронированию.", userId));
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(BookingStatus.APPROVED, bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(BookingStatus.REJECTED, bookingId);
        }
        return booking;
    }
}