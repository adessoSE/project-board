package de.adesso.projectboard.base.reader;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * A {@link HealthIndicator} implementation for the {@link ProjectReader} bean
 * with name "projectReaderBean".
 *
 * @see ProjectReader#health()
 */
@Component
public class ReaderHealthIndicator implements HealthIndicator {

    private final ProjectReader projectReader;

    public ReaderHealthIndicator(ProjectReader projectReader) {
        this.projectReader = projectReader;
    }

    /**
     *
     * @return
     *          The {@link Health} of the {@link ProjectReader}.
     *
     * @see ProjectReader#health()
     */
    @Override
    public Health health() {
        return projectReader.health();
    }

}
