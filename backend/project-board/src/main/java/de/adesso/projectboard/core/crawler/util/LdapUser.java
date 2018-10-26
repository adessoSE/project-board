package de.adesso.projectboard.core.crawler.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LdapUser {

    private String sAMAccountName;

    private String name;

    private String mail;

    private String division;

    private String manager;

}
