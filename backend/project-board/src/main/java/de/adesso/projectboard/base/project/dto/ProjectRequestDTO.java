package de.adesso.projectboard.base.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectRequestDTO {

    @NotEmpty
    @Size(max = 255)
    private String status;

    @NotEmpty
    @Size(max = 255)
    private String issuetype;

    @NotEmpty
    @Size(max = 255)
    private String title;

    private List<String> labels = new ArrayList<>();

    @Size(max = 8192)
    private String job;

    @Size(max = 8192)
    private String skills;

    @NotEmpty
    @Size(max = 8192)
    private String description;

    @NotEmpty
    @Size(max = 255)
    private String lob;

    @Size(max = 255)
    private String customer;

    @Size(max = 255)
    private String location;

    @Size(max = 255)
    private String operationStart;

    @Size(max = 255)
    private String operationEnd;

    @Size(max = 255)
    private String effort;

    @Size(max = 255)
    private String freelancer;

    @Size(max = 255)
    private String elongation;

    @Size(max = 8192)
    private String other;

}
