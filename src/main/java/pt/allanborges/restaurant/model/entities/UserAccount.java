package pt.allanborges.restaurant.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pt.allanborges.restaurant.model.enums.Role;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "USERS",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_USERS_USERNAME", columnNames = {"USERNAME"}),
                @UniqueConstraint(name = "UK_USERS_EMAIL", columnNames = {"EMAIL"}),
                @UniqueConstraint(name = "UK_USERS_NIF", columnNames = {"NIF"})
        }
)
public class UserAccount extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    @Column(name = "USERNAME", length = 80, unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(name = "PASSWORD_HASH", length = 200, nullable = false)
    private String passwordHash;

    @NotBlank
    @Column(name = "NAME", length = 120, nullable = false)
    private String name;

    @NotBlank
    @Email
    @Column(name = "EMAIL", length = 180, nullable = false, unique = true)
    private String email;

    @Column(name = "NIF", length = 20, unique = true)
    private String nif;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "USER_ID"))
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", length = 20, nullable = false)
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    @Column(name = "ENABLED", nullable = false)
    private boolean enabled = false;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    /* ------------ Helpers to maintain invariants ------------ */

    public void setAddresses(List<Address> newAddresses) {
        this.addresses.clear();
        if (newAddresses != null) {
            newAddresses.forEach(a -> a.setUser(this));
            ensureSinglePrimary(newAddresses);
            this.addresses.addAll(newAddresses);
        }
    }

    public void addAddress(Address a) {
        a.setUser(this);
        if (a.isPrimaryAddress()) {
            this.addresses.forEach(addr -> addr.setPrimaryAddress(false));
        }
        this.addresses.add(a);
    }

    private static void ensureSinglePrimary(List<Address> addrs) {
        long primaries = addrs.stream().filter(Address::isPrimaryAddress).count();
        if (primaries > 1) {
            throw new IllegalArgumentException("Only one primary address is allowed per user.");
        }
        // Optional: if none is primary, auto-select the first as primary
        if (primaries == 0 && !addrs.isEmpty()) {
            addrs.get(0).setPrimaryAddress(true);
        }
    }

}