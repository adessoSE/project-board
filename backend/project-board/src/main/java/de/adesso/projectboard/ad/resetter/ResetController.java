package de.adesso.projectboard.ad.resetter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @DeleteMapping(path = "/userandorgdata")
    @PreAuthorize("hasRole('admin')")
    public void resetCachedData() {
        resetService.resetCachedUserDataAndOrgStructures();
    }

}
