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
import dev.xframe.inject.Providable;
import dev.xframe.utils.XOptional;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Providable
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
        SettingColumns.reg("xframe.admin.ldap.url", EColumn.Text, "Ldap.url");
        SettingColumns.reg("xframe.admin.ldap.principal", EColumn.Text, "Ldap.principal");
        SettingColumns.reg("xframe.admin.ldap.maildomain", EColumn.Text, "Ldap.maildomain");
    }
    private void reloadSettings() {
        if(XOptional.mapToBool(sysRepo.getSetting("xframe.admin.ldap"), Boolean::parseBoolean, false)) {
            ldapConfig.url      = sysRepo.getSetting("xframe.admin.ldap.url");
            ldapConfig.principal= sysRepo.getSetting("xframe.admin.ldap.principal");
            ldapConfig.maildomain = sysRepo.getSetting("xframe.admin.ldap.maildomain");
            if(ldapConfig.isIntact() && uiRegistered.compareAndSet(false, true)) {
                UserInterfaces.reg(2, "Ldap", this);
            }
        }
    }

    @Override
    public void validate(String username, String password) {
        if(!LdapHelper.validate(ldapConfig, username, password)) {
            throw new LogicException("Ldap: username or password incorrect");
        }
    }

    @Override
    public String makeEmail(String username) {
        return username + "@" + ldapConfig.maildomain;
    }
}
