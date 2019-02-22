package de.adesso.projectboard.rest.handler.application;

import de.adesso.projectboard.base.application.handler.ProjectApplicationEventHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import de.adesso.projectboard.rest.handler.mail.MailService;
import de.adesso.projectboard.rest.handler.mail.persistence.ApplicationTemplateMessage;
import de.adesso.projectboard.rest.handler.mail.persistence.TemplateMessage;
import de.adesso.projectboard.rest.security.KeycloakAuthenticationInfoRetriever;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * A {@link ProjectApplicationEventHandler} implementation that sends out a mail to the
 * supervisor of the applicant.
 *
 * <p>
 *     Activated via the <i>mail</i> spring profile.
 * </p>
 *
 * @see MailService
 * @see ApplicationTemplateMessage
 * @see KeycloakAuthenticationInfoRetriever
 */
@Profile("mail")
@Component
public class ProjectBoardApplicationEventHandler implements ProjectApplicationEventHandler {

    private final MailService mailService;

    private final UserService userService;

    public ProjectBoardApplicationEventHandler(MailService mailService, UserService userService) {
        this.mailService = mailService;
        this.userService = userService;
    }

    @Override
    public void onApplicationReceived(ProjectApplication application) {
        User user = application.getUser();
        User manager = userService.getManagerOfUser(user);

        UserData userData = userService.getUserData(user);
        UserData managerData = userService.getUserData(manager);

        TemplateMessage message = new ApplicationTemplateMessage(application, managerData, userData);

        mailService.queueMessage(message);
    }

}
