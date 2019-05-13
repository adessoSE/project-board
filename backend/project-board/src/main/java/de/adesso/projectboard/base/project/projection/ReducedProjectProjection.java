package de.adesso.projectboard.base.project.projection;

import java.time.LocalDateTime;
import java.util.List;

public interface ReducedProjectProjection {

    String getId();

    String getStatus();

    String getIssuetype();

    String getTitle();

    List<String> getLabels();

    String getJob();

    String getSkills();

    String getDescription();

    String getLob();

    String getCustomer();

    String getLocation();

    String getOperationStart();

    String getOperationEnd();

    String getEffort();

    LocalDateTime getCreated();

    LocalDateTime getUpdated();

    String getElongation();

    String getOther();

}
