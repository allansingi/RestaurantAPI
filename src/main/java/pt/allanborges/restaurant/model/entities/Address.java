package pt.allanborges.restaurant.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "USER_ADDRESSES")
public class Address extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserAccount user;

    @NotBlank
    @Column(name = "STREET_NAME", length = 200, nullable = false)
    private String streetName;

    @Column(name = "DOOR_NUMBER", length = 30)
    private String doorNumber;

    @NotBlank
    @Column(name = "POSTAL_CODE", length = 20, nullable = false)
    private String postalCode;

    @NotBlank
    @Column(name = "DISTRICT", length = 100, nullable = false)
    private String district;

    @NotBlank
    @Column(name = "MUNICIPALITY", length = 100, nullable = false)
    private String municipality;

    @Column(name = "NEIGHBORHOOD", length = 100)
    private String neighborhood;

    @Builder.Default
    @Column(name = "IS_PRIMARY", nullable = false)
    private boolean primaryAddress = false;

}