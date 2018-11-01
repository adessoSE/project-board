package de.adesso.projectboard.base.reader;

import de.adesso.projectboard.base.project.persistence.Project;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * {@link ProjectReader} implementation that performs no action and returns a {@link Collections#emptyList() empty list}
 * in both {@link ProjectReader#getAllProjectsSince(LocalDateTime)} and {@link ProjectReader#getInitialProjects()}
 * implementations.
 *
 * <p>
 *     Auto-configured {@link ProjectReader} bean when no other bean is present.
 * </p>
 *
 * @see ProjectReader
 */
public class NoActionProjectReader implements ProjectReader {

    /**
     *
     * @param dateTime
     *          The {@link LocalDateTime} of the last successful update.
     *
     * @return
     *          A empty {@link List} returned by {@link Collections#emptyList()}.
     *
     */
    @Override
    public List<? extends Project> getAllProjectsSince(LocalDateTime dateTime) {
        return Collections.emptyList();
    }

    /**
     *
     * @return
     *          A empty {@link List} returned by {@link Collections#emptyList()}.
     */
    @Override
    public List<? extends Project> getInitialProjects() {
        return Collections.emptyList();
    }

}
