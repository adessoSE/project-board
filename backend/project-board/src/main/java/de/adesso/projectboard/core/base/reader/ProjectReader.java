package de.adesso.projectboard.core.base.reader;

import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import org.springframework.boot.actuate.health.Health;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A specification for a reader that is used in {@link de.adesso.projectboard.core.base.updater.ProjectDatabaseUpdater}.
 *
 * @see de.adesso.projectboard.core.reader.JiraProjectReader
 */
public interface ProjectReader {

    /**
     * This method gets invoked by the {@link de.adesso.projectboard.core.base.updater.ProjectDatabaseUpdater}
     * to update the projects in the database.
     *
     * @param dateTime
     *          The {@link LocalDateTime} of the last successful update.
     *
     * @return
     *          A list of {@link AbstractProject}s.
     *
     * @throws Exception
     *          When a error occurrs.
     *
     * @see de.adesso.projectboard.core.base.updater.ProjectDatabaseUpdater
     */
    List<? extends AbstractProject> getAllProjectsSince(LocalDateTime dateTime) throws Exception;

    /**
     * This method gets invoked by the {@link de.adesso.projectboard.core.base.updater.ProjectDatabaseUpdater}
     * to get the initial list of projects when no successful update was performed before.
     *
     * @return
     *         A list of {@link AbstractProject}s.
     *
     * @throws Exception
     *          When a error occurrs.
     */
    List<? extends AbstractProject> getInitialProjects() throws Exception;

    /**
     * This method is invoked by the {@link ReaderHealthIndicator} to get
     * the {@link Health} of the reader. Always returns {@link Health#up()}
     * in its default implementation.
     *
     * @return
     *          The {@link Health} of the reader.
     *
     * @see ReaderHealthIndicator
     */
    default Health health() {
        return Health.up()
                .build();
    }

}
