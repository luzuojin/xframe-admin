package dev.xframe.admin.system.user.ldap;

import dev.xframe.utils.XLogger;
import dev.xframe.utils.XStrings;

import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

public class LdapHelper {

    /**
     * 通过uid获取dn,然后连接验证
     */
    public static boolean validate(LdapConfig cfg, String uid, String password) {
        LdapContext ctx = null;
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, cfg.url);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL,  "uid=" + uid + "," + cfg.principal);
            env.put(Context.SECURITY_CREDENTIALS, password);
            ctx = new InitialLdapContext(env, null);
            return true;
        } catch (Throwable e) {
            XLogger.warn("Ldap user[{}] validate failed cause:\n{}", uid, XStrings.getStackTrace(e));
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Throwable e) {
                    XLogger.debug("Ldap connection close error:", e);
                }
            }
        }
        return false;
    }

}
