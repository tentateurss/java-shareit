package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Дрель");
        dto.setDescription("Электрическая дрель");
        dto.setAvailable(true);

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id", 1);
        assertThat(result).hasJsonPathStringValue("$.name", "Дрель");
        assertThat(result).hasJsonPathStringValue("$.description", "Электрическая дрель");
        assertThat(result).hasJsonPathBooleanValue("$.available", true);
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Дрель\",\n" +
                "  \"description\": \"Электрическая дрель\",\n" +
                "  \"available\": true\n" +
                "}";

        ItemDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("Электрическая дрель");
        assertThat(dto.getAvailable()).isTrue();
    }
}