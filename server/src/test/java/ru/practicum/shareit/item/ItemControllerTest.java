package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .requestId(1L)
            .build();

    private final List<ItemDto> itemsDtoList = List.of(
            new ItemDto(1L, "Name", "Description", true, null,
                    null, null, null),
            new ItemDto(2L, "Name2", "Description2", true, null,
                    null, null, null));
    private final CommentDto commentDto = CommentDto.builder().id(1L).text("Text").authorName("Name").build();

    @Test
    void createItemWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).create(1L, itemDto);
    }

    @Test
    void createItemWhenItemWithoutNameThenReturnedStatusIsBadRequest() throws Exception {
        ItemDto badItemDto = ItemDto.builder()
                .id(1L)
                .name("")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();

        when(itemService.create(anyLong(), any()))
                .thenReturn(badItemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badItemDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).create(1L, badItemDto);
    }

    @Test
    void createItemWhenItemWithoutAvailbleThenReturnedStatusIsBadRequest() throws Exception {
        ItemDto badItemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("description")
                .requestId(1L)
                .build();
        when(itemService.create(anyLong(), any()))
                .thenReturn(badItemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badItemDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).create(1L, badItemDto);
    }

    @Test
    void findItemByIdWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).findItemById(1L, 1L);
    }

    @Test
    void findItemByIdWhenItemIdIsNotFoundThenReturnedStatusIsNotFound() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException(String.format("Вещь с ID = %d не найден.", 100L)));

        mvc.perform(get("/items/{id}", 100L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findAllUsersItemsWhenUserIdIsExistThenReturnedStatusIsOk() throws Exception {
        when(itemService.findAllUsersItems(anyLong()))
                .thenReturn(itemsDtoList);

        String result = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(itemsDtoList));

    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto updateItemDto = ItemDto.builder()
                .id(1L)
                .name("updateItem")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();

        when(itemService.save(any(), anyLong(), anyLong()))
                .thenReturn(updateItemDto);

        mvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).save(updateItemDto, 1L, 1L);
    }

    @Test
    void searchItemByParams() throws Exception {
        when(itemService.search(anyString()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=descr", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Item")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Description")));
    }

    @Test
    void addCommentWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        when(itemService.addComment(any(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void addCommentWhenTextIsEmptyThenReturnedStatusIsBadRequest() throws Exception {
        CommentDto badCommentDto = CommentDto.builder().id(1L).text("").authorName("AuthorName").build();
        when(itemService.addComment(any(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(badCommentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteItemTest() throws Exception {
        mvc.perform(delete("/items/{itemId}", 1L))
                .andExpect(status().isOk());

        verify(itemService).deleteById(1L);
    }
}