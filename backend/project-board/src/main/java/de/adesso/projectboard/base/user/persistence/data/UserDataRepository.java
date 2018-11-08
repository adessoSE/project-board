package de.adesso.projectboard.base.user.persistence.data;

import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * {@link JpaRepository} to persist {@link UserData} instances.
 */
public interface UserDataRepository extends JpaRepository<UserData, Long> {

    Optional<UserData> findByUser(User user);

    List<UserData> findByUserIn(Collection<User> users, Sort sort);

}
