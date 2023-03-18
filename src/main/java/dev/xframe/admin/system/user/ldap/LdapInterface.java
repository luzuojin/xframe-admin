package dev.xframe.admin.system.user.ldap;

import dev.xframe.admin.conf.LogicException;
import dev.xframe.admin.system.SystemRepo;
import dev.xframe.admin.system.settings.SettingColumns;
import dev.xframe.admin.system.user.UserInterface;
import dev.xframe.admin.system.user.UserInterfaces;
import dev.xframe.admin.view.EColumn;
import dev.xframe.inject.Component;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;
import dev.xframe.utils.XOptional;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class LdapInterface implements Loadable, UserInterface {

    @Inject
    private SystemRepo sysRepo;

    private AtomicBoolean uiRegistered = new AtomicBoolean(false);

    private LdapConfig ldapConfig = new LdapConfig();

    @Override
    public void load() {
        registSettings();
        reloadSettings();
    }
    private void registSettings() {
        SettingColumns.reg("xframe.admin.ldap", EColumn.Bool, "Ldap", s->reloadSettings());
        SettingColumns.reg("xframe.admin.ldap.url", EColumn.Text, "Ldap.URL");
        SettingColumns.reg("xframe.admin.ldap.user.dn", EColumn.Text, "Ldap.UserDN");
        SettingColumns.reg("xframe.admin.ldap.root.dn", EColumn.Text, "Ldap.RootDN");
        SettingColumns.reg("xframe.admin.ldap.password", EColumn.Text, "Ldap.Password");
        SettingColumns.reg("xframe.admin.ldap.domain", EColumn.Text, "Ldap.Domain");
    }
    private void reloadSettings() {
        if(XOptional.mapToBool(sysRepo.getSetting("xframe.admin.ldap"), Boolean::parseBoolean, false)) {
            ldapConfig.url      = sysRepo.getSetting("xframe.admin.ldap.url");
            ldapConfig.userDn   = sysRepo.getSetting("xframe.admin.ldap.user.dn");
            ldapConfig.rootDn   = sysRepo.getSetting("xframe.admin.ldap.root.dn");
            ldapConfig.password = sysRepo.getSetting("xframe.admin.ldap.password");
            ldapConfig.domain   = sysRepo.getSetting("xframe.admin.ldap.domain");
            if(ldapConfig.isIntact() && uiRegistered.compareAndSet(false, true)) {
                UserInterfaces.reg(1, "Ldap", this);
            }
        }
    }

    @Override
    public void validate(String username, String password) {
        if(!new LdapHelper(ldapConfig).validate(username, password)) {
            throw new LogicException("Ldap: username or password incorrect");
        }
    }

    @Override
    public String makeEmail(String username) {
        return username + "@" + ldapConfig.domain;
    }

}
