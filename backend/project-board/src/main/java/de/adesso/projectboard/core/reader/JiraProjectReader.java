package de.adesso.projectboard.core.reader;

import de.adesso.projectboard.core.base.project.AbstractProject;
import de.adesso.projectboard.core.base.reader.AbstractProjectReader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JiraProjectReader extends AbstractProjectReader {

    @Override
    public List<? extends AbstractProject> getUpdatedProjectsSince(LocalDateTime dateTime) {
        return null;
    }

    @Override
    public List<? extends AbstractProjectReader> getAllProjectsSince(LocalDateTime dateTime) {
        return null;
    }

}
