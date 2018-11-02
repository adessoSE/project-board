package de.adesso.projectboard.ldap.service.util.data;

import de.adesso.projectboard.base.user.persistence.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StringStructure {

    private User user;

    private String manager;

    private Set<String> staffMembers = new HashSet<>();

}
