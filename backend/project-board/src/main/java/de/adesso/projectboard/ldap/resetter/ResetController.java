package de.adesso.projectboard.ldap.resetter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link ResetController REST Controller} delete all {@link de.adesso.projectboard.base.user.persistence.structure.OrganizationStructure} and
 * {@link de.adesso.projectboard.base.user.persistence.data.UserData} instances from their corresponding
 * repositories via the {@link ResetService}.
 *
 * @see ResetService
 */
@Profile("adesso-ad")
@RestController
@RequestMapping(path = "/reset")
public class ResetController {

    private final ResetService resetService;

    @Autowired
    public ResetController(ResetService resetService) {
        this.resetService = resetService;
    }

    // run at 4 am every day (monday till friday)
    @Scheduled(cron = "0 0 4 * * MON-FRI")
    @GetMapping(path = "/all")
    @PreAuthorize("hasRole('admin')")
    public void resetCachedData() {
        resetService.resetCachedData();
    }

}
