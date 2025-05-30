package inventorymanagementsystem.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, AccountStatus status);

    @Procedure("delete_unverified_users_with_expired_tokens")
    void deleteUnverifiedUsersWithExpiredTokens();

}
