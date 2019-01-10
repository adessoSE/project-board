package de.adesso.projectboard.ldap.resetter;

import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.hierarchy.OrganizationStructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to delete all {@link de.adesso.projectboard.base.user.persistence.hierarchy.OrganizationStructure} and
 * {@link de.adesso.projectboard.base.user.persistence.data.UserData} instances from their corresponding
 * repositories.
 *
 * @see ResetController
 */
@Profile("adesso-ad")
@Service
public class ResetService {

    private final UserDataRepository dataRepo;

    private final OrganizationStructureRepository orgRepo;

    @Autowired
    public ResetService(UserDataRepository dataRepo, OrganizationStructureRepository orgRepo) {
        this.dataRepo = dataRepo;
        this.orgRepo = orgRepo;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void resetCachedUserDataAndOrgStructures() {
        dataRepo.deleteAll();
        orgRepo.deleteAll();
    }

}
