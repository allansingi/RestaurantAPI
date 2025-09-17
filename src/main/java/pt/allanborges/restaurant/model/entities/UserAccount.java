package pt.allanborges.restaurant.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pt.allanborges.restaurant.model.enums.Role;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "USERS")
public class UserAccount extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    @Column(name = "USERNAME", length = 80, unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(name = "PASSWORD_HASH", length = 200, nullable = false)
    private String passwordHash;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "USER_ID"))
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", length = 20, nullable = false)
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    @Column(name = "ENABLED", nullable = false)
    private boolean enabled = false;

}