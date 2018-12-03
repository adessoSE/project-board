package de.adesso.projectboard.base.project.dto;

import de.adesso.projectboard.base.project.persistence.Project;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Mapper class to map {@link ProjectRequestDTO}s to {@link Project}s.
 */
@Component
public class ProjectDtoMapper {

    private final Clock clock;

    /**
     * Default constructor that sets the {@link #clock} to
     * the {@link Clock#systemDefaultZone()}.
     */
    public ProjectDtoMapper() {
        this.clock = Clock.systemDefaultZone();
    }

    /**
     *
     * @param clock
     *          The {@link Clock} to use to get the
     *          current time with {@link LocalDateTime#now(Clock)}.
     */
    public ProjectDtoMapper(Clock clock) {
        this.clock = clock;
    }

    /**
     * Creates a new {@link Project} from a {@link ProjectRequestDTO DTO}.
     * The {@link Project#created creation} and {@link Project#updated update}
     * time are set to the {@link LocalDateTime#now(Clock) current time} according
     * to the given {@link #clock}.
     *
     * The {@link Project#id project ID} and the {@link Project#origin project origin}
     * are <b>not set</b> explicitly!
     *
     * @param dto
     *          The {@link ProjectRequestDTO DTO} to create the {@link Project}
     *          from.
     *
     * @return
     *          The created {@link Project}.
     */
    public Project toProject(ProjectRequestDTO dto) {
        LocalDateTime createdUpdatedTime = LocalDateTime.now(clock);

        return new Project()
                .setStatus(dto.getStatus())
                .setIssuetype(dto.getIssuetype())
                .setTitle(dto.getTitle())
                .setLabels(dto.getLabels())
                .setJob(dto.getJob())
                .setSkills(dto.getSkills())
                .setDescription(dto.getDescription())
                .setLob(dto.getLob())
                .setCustomer(dto.getCustomer())
                .setLocation(dto.getLocation())
                .setOperationStart(dto.getOperationStart())
                .setOperationEnd(dto.getOperationEnd())
                .setEffort(dto.getEffort())
                .setCreated(createdUpdatedTime)
                .setUpdated(createdUpdatedTime)
                .setFreelancer(dto.getFreelancer())
                .setElongation(dto.getElongation())
                .setOther(dto.getOther());
    }

}
