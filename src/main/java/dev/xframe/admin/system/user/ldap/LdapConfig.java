package dev.xframe.admin.system.user.ldap;

import dev.xframe.utils.XStrings;

public class LdapConfig {

    public String url;          //ldap://127.0.0.1:389/
    public String principal;    //ou=people
    public String maildomain;   //xframe.dev (mail domain)

    public boolean isIntact() {
        return !(XStrings.isBlank(url) || XStrings.isBlank(principal) || XStrings.isBlank(maildomain));
    }
}
