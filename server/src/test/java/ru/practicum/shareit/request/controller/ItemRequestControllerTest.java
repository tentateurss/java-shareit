package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createRequestShouldReturnStatusOk() throws Exception {
        long userId = 1L;
        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setDescription("Нужна дрель");

        ItemRequestDto outputDto = new ItemRequestDto();
        outputDto.setId(10L);
        outputDto.setDescription("Нужна дрель");
        outputDto.setCreated(LocalDateTime.now());

        when(requestService.create(eq(userId), any(ItemRequestDto.class))).thenReturn(outputDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outputDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(outputDto.getDescription())));
    }

    @Test
    void findByRequesterShouldReturnList() throws Exception {
        long userId = 1L;
        ItemRequestDto outputDto = new ItemRequestDto();
        outputDto.setId(10L);
        outputDto.setDescription("Нужна дрель");

        when(requestService.findByRequester(userId)).thenReturn(List.of(outputDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(outputDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(outputDto.getDescription())));
    }

    @Test
    void findFromOthersShouldReturnList() throws Exception {
        long userId = 1L;
        ItemRequestDto outputDto = new ItemRequestDto();
        outputDto.setId(20L);
        outputDto.setDescription("Нужен перфоратор");

        when(requestService.findFromOthers(userId)).thenReturn(List.of(outputDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(outputDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(outputDto.getDescription())));
    }

    @Test
    void findByIdShouldReturnRequest() throws Exception {
        long userId = 1L;
        long requestId = 10L;
        ItemRequestDto outputDto = new ItemRequestDto();
        outputDto.setId(requestId);
        outputDto.setDescription("Нужен триммер");

        when(requestService.findById(userId, requestId)).thenReturn(outputDto);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outputDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(outputDto.getDescription())));
    }
}