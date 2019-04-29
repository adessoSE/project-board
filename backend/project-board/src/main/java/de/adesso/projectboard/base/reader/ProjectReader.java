package de.adesso.projectboard.base.reader;

import de.adesso.projectboard.base.project.persistence.Project;
import org.springframework.boot.actuate.health.Health;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A specification for a reader that is used in {@link de.adesso.projectboard.base.project.updater.ProjectUpdater}.
 *
 * @see de.adesso.projectboard.reader.JiraProjectReader
 */
public interface ProjectReader {

    /**
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
     * @see de.adesso.projectboard.base.project.updater.ProjectUpdater
     */
    List<Project> getAllProjectsSince(LocalDateTime dateTime) throws Exception;

    /**
     *
     * @return
     *         A list of {@link Project}s.
     *
     * @throws Exception
     *          When a error occurs.
     */
    List<Project> getInitialProjects() throws Exception;

    /**
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
