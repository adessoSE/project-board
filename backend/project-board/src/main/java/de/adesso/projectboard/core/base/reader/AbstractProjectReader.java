package de.adesso.projectboard.core.base.reader;

import de.adesso.projectboard.core.base.project.persistence.AbstractProject;

import java.time.LocalDateTime;
import java.util.List;

public interface AbstractProjectReader {

    List<? extends AbstractProject> getAllProjectsSince(LocalDateTime dateTime) throws Exception;

}
