package pt.allanborges.restaurant.model.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private Long id;
    @NotBlank(message = "Street name is required")
    @Size(max = 200, message = "Street name max 200 characters")
    private String streetName;
    @Size(max = 30, message = "Door number max 30 characters")
    private String doorNumber;
    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code max 20 characters")
    private String postalCode;
    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District max 100 characters")
    private String district;
    @NotBlank(message = "Municipality is required")
    @Size(max = 100, message = "Municipality max 100 characters")
    private String municipality;
    @Size(max = 100, message = "Neighborhood max 100 characters")
    private String neighborhood;
    private Boolean primaryAddress;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;
    private String inactivatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime inactivatedDate;

}