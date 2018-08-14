package de.adesso.projectboard.core.base.reader;

import de.adesso.projectboard.core.base.project.AbstractProject;

import java.time.LocalDateTime;
import java.util.List;

public abstract class AbstractProjectReader {

    public abstract List<?extends AbstractProject> getUpdatedProjectsSince(LocalDateTime dateTime);

    public abstract List<? extends AbstractProjectReader> getAllProjectsSince(LocalDateTime dateTime);

}
