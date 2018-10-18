package de.adesso.projectboard.util;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectOrigin;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Getter
public class ProjectSupplier {

    private Project editableProject;

    private Project nonEditableProject;

    public ProjectSupplier() {
        setUpProjects();
    }

    public void resetProjects() {
        setUpProjects();
    }

    private void setUpProjects() {
        this.editableProject = new Project()
                .setId("STF-9")
                .setStatus("Offen")
                .setIssuetype("Original Issuetype")
                .setTitle("Original Title")
                .setLabels(Arrays.asList("Original Label 1", "Original Label 2"))
                .setJob("Original Job")
                .setSkills("Original Skills")
                .setDescription("Original Description")
                .setLob("LOB Prod")
                .setCustomer("Original Customer")
                .setLocation("Original Location")
                .setOperationStart("Original Start")
                .setOperationEnd("Original End")
                .setEffort("Original Effort")
                .setCreated(LocalDateTime.now().minus(10L, ChronoUnit.DAYS))
                .setUpdated(LocalDateTime.now().minus(3L, ChronoUnit.DAYS))
                .setFreelancer("Original Freelancer")
                .setElongation("Original Elongation")
                .setOther("Original Other")
                .setOrigin(ProjectOrigin.CUSTOM);

        this.nonEditableProject = new Project()
                .setId("STF-10")
                .setStatus("eskaliert")
                .setIssuetype("Original Issuetype")
                .setTitle("Original Title")
                .setLabels(Arrays.asList("Original Label 1", "Original Label 2"))
                .setJob("Original Job")
                .setSkills("Original Skills")
                .setDescription("Original Description")
                .setLob("LOB Prod")
                .setCustomer("Original Customer")
                .setLocation("Original Location")
                .setOperationStart("Original Start")
                .setOperationEnd("Original End")
                .setEffort("Original Effort")
                .setCreated(LocalDateTime.now().minus(10L, ChronoUnit.DAYS))
                .setUpdated(LocalDateTime.now().minus(3L, ChronoUnit.DAYS))
                .setFreelancer("Original Freelancer")
                .setElongation("Original Elongation")
                .setOther("Original Other")
                .setOrigin(ProjectOrigin.JIRA);
    }

}
