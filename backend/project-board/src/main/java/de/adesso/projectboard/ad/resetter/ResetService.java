package de.adesso.projectboard.ad.resetter;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Profile("adesso-ad")
@Service
public class ResetService {

//    private final UserDataRepository dataRepo;
//
//    private final OrganizationStructureRepository orgRepo;
//
//    @Autowired
//    public ResetService(UserDataRepository dataRepo, OrganizationStructureRepository orgRepo) {
//        this.dataRepo = dataRepo;
//        this.orgRepo = orgRepo;
//    }
//
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void resetCachedUserDataAndOrgStructures() {
//        dataRepo.deleteAll();
//        orgRepo.deleteAll();
    }

}
