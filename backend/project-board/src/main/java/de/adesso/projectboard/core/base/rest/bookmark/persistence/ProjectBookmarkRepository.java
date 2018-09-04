package de.adesso.projectboard.core.base.rest.bookmark.persistence;

import org.springframework.data.repository.CrudRepository;

/**
 * {@link CrudRepository} to persist {@link ProjectBookmark}s.
 */
public interface ProjectBookmarkRepository extends CrudRepository<ProjectBookmark, Long> {

}
