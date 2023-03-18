package dev.xframe.admin.system.user.ldap;

import dev.xframe.utils.XStrings;

public class LdapConfig {

    public String url;      //ldap://127.0.0.1:389/
    public String userDn;   //ou=people
    public String rootDn;   //cn=manager
    public String password; //manager password
    public String domain;   //xframe.dev (mail domain)

    public boolean isIntact() {
        return !(XStrings.isBlank(url) || XStrings.isBlank(userDn) || XStrings.isBlank(rootDn) || XStrings.isBlank(password) || XStrings.isBlank(domain));
    }
}
