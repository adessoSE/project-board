package de.adesso.projectboard.ad.service.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LdapUserNode {

    String id;

    String name;

    String givenName;

    String surname;

    String userPrincipalName;

    String division;

    String department;

    String dn;

    String managerDn;

    List<String> directReportsDn = new ArrayList<>();

}
