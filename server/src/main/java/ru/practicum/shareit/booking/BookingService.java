package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import java.util.List;

public interface BookingService {
    OutputBookingDto create(InputBookingDto bookingDtoShort, Long bookerId);

    OutputBookingDto findBookingById(Long bookingId, Long userId);

    List<OutputBookingDto> findAllBookingsByUser(String state, Long userId);

    List<OutputBookingDto> findAllBookingsByOwner(String state, Long ownerId);

    OutputBookingDto approve(long bookingId, long userId, Boolean approve);
}