package pt.allanborges.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.allanborges.restaurant.model.entities.UserAccount;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(final String username);
    Optional<UserAccount> findByEmail(final String email);
    Optional<UserAccount> findByNif(final String nif);
}