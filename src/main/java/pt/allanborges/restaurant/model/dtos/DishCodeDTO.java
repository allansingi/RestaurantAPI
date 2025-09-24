package pt.allanborges.restaurant.model.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = DishCodeDTO.Deserializer.class)
public class DishCodeDTO {
    private Long id;

    @NotBlank(message = "Code is required")
    @Size(min = 3, max = 64, message = "Code must be 3–64 characters")
    @Pattern(regexp = "^[A-Z0-9_\\-]+$", message = "Code must be UPPERCASE letters, digits, _ or -")
    private String code;

    @Size(max = 200, message = "Description max 200 characters")
    private String description;

    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;
    private String inactivatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime inactivatedDate;

    public static class Deserializer extends JsonDeserializer<DishCodeDTO> {
        @Override
        public DishCodeDTO deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            if (p.currentToken() == JsonToken.VALUE_STRING) {
                return DishCodeDTO.builder().code(p.getValueAsString()).build();
            }
            JsonNode node = p.getCodec().readTree(p);
            DishCodeDTO dto = new DishCodeDTO();
            if (node.hasNonNull("code")) dto.setCode(node.get("code").asText());
            if (node.hasNonNull("description")) dto.setDescription(node.get("description").asText());
            return dto;
        }
    }

}