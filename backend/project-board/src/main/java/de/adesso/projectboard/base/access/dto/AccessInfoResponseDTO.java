package de.adesso.projectboard.base.access.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessInfoResponseDTO implements Serializable {

    private boolean hasAccess = false;

    private LocalDateTime accessStart;

    private LocalDateTime accessEnd;

}
