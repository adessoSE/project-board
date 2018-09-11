package de.adesso.projectboard.core.rest.useraccess.dto;

import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.rest.useraccess.persistence.UserAccessInfo;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class UserAccessInfoResponseDTOTest {

    @Test
    public void fromAccessInfo_ShouldHaveAccess() {
        User user = new User("user");

        LocalDateTime startTime = LocalDateTime.now().minus(10L, ChronoUnit.DAYS);
        LocalDateTime endTime = LocalDateTime.now().plus(10L, ChronoUnit.DAYS);

        UserAccessInfo info = new UserAccessInfo(user, endTime);
        info.setAccessStart(startTime);
        info.setId(1L);

        UserAccessInfoResponseDTO dto = UserAccessInfoResponseDTO.fromAccessInfo(info);

        assertTrue(dto.isHasAccess());
        assertEquals(startTime, dto.getAccessStart());
        assertEquals(endTime, dto.getAccessEnd());
    }

    @Test
    public void fromAccessInfo_ShouldHaveNoAccess() {
        User user = new User("user");

        LocalDateTime startTime = LocalDateTime.now().minus(25L, ChronoUnit.DAYS);
        LocalDateTime endTime = LocalDateTime.now().plus(1L, ChronoUnit.DAYS);

        UserAccessInfo info = new UserAccessInfo(user, endTime);
        info.setAccessStart(startTime);
        info.setId(1L);

        UserAccessInfoResponseDTO dto = UserAccessInfoResponseDTO.fromAccessInfo(info);

        assertFalse(dto.isHasAccess());
        assertEquals(startTime, dto.getAccessStart());
        assertEquals(endTime, dto.getAccessEnd());
    }

}