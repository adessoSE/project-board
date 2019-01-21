package de.adesso.projectboard.ad.updater.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserUpdateJobRepository extends JpaRepository<UserUpdateJob, Long> {

    Optional<UserUpdateJob> findFirstBySuccessTrueOrderByUpdateTimeDesc();

}
