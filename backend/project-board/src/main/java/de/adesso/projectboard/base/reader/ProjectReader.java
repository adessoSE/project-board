package de.adesso.projectboard.base.reader;

import de.adesso.projectboard.base.project.persistence.Project;
import org.springframework.boot.actuate.health.Health;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A specification for a reader that is used in {@link de.adesso.projectboard.base.updater.ProjectDatabaseUpdater}.
 *
 * @see de.adesso.projectboard.reader.JiraProjectReader
 */
public interface ProjectReader {

    /**
     * This method gets invoked by the {@link de.adesso.projectboard.base.updater.ProjectDatabaseUpdater}
     * to update the projects in the database.
     *
     * @param dateTime
     *          The {@link LocalDateTime} of the last <b>successful</b> update.
     *
     * @return
     *          A list of {@link Project}s.
     *
     * @throws Exception
     *          When a error occurs.
     *
     * @see de.adesso.projectboard.base.updater.ProjectDatabaseUpdater
     */
    List<Project> getAllProjectsSince(LocalDateTime dateTime) throws Exception;

    /**
     * This method gets invoked by the {@link de.adesso.projectboard.base.updater.ProjectDatabaseUpdater}
     * to get the initial list of projects when no successful update was performed before.
     *
     * @return
     *         A list of {@link Project}s.
     *
     * @throws Exception
     *          When a error occurrs.
     */
    List<Project> getInitialProjects() throws Exception;

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
