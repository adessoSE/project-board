package de.adesso.projectboard.core.base.reader;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * A {@link HealthIndicator} implementation for the {@link AbstractProjectReader} bean
 * with name "projectReaderBean".
 *
 * @see AbstractProjectReader#health()
 */
@Component
public class ReaderHealthIndicator implements HealthIndicator {

    private final AbstractProjectReader projectReader;

    public ReaderHealthIndicator(AbstractProjectReader projectReader) {
        this.projectReader = projectReader;
    }

    /**
     *
     * @return
     *          The {@link Health} of the {@link AbstractProjectReader}.
     *
     * @see AbstractProjectReader#health()
     */
    @Override
    public Health health() {
        return projectReader.health();
    }

}
