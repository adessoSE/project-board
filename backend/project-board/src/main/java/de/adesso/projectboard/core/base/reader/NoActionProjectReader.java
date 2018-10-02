package de.adesso.projectboard.core.base.reader;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * {@link ProjectReader} implementation that performs no action.
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
