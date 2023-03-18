package dev.xframe.admin.system.user.ldap;

import dev.xframe.utils.XLogger;
import dev.xframe.utils.XStrings;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

public class LdapHelper {

    private LdapConfig cfg;
    /**
     * LDAP可以理解为一个多级目录，这里，表示要连接到那个具体的目录
     */
    private LdapContext ctx;

    public LdapHelper(LdapConfig cfg) {
        this.cfg = cfg;
        this.ctx = connect();
    }

    LdapContext connect() {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, cfg.url + cfg.userDn);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, cfg.rootDn);
            env.put(Context.SECURITY_CREDENTIALS, cfg.password);
            return new InitialLdapContext(env, null);
        } catch (Exception e) {
            XLogger.warn("Ldap connect failed, cause:", e);
        }
        return null;
    }

    void close() {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException e) {
                XLogger.debug("Ldap connection close error:", e);
            }
        }
    }

    /**
     * dn就是目标名字+当前目录名字
     */
    private String queryUserDN(String uid) {
        StringBuilder userDN = new StringBuilder();
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            //第一个参数是从哪个目录下开始查，因为连接的url里已经指定了ou=People,dc=gosun,dc=com，所以这里填""即可
            //第二个参数是查询条件，uid、email等，都是ldap中存放的参数
            NamingEnumeration<SearchResult> en = ctx.search("", "uid=" + uid, constraints);
            if (en == null || !en.hasMoreElements()) {
                XLogger.warn("Ldap user[{}] not found." + uid);
            }
            while (en != null && en.hasMoreElements()) {
                SearchResult sr = en.nextElement();
                if (sr != null) {
                    userDN.append(sr.getName()).append(",").append(cfg.userDn);
                }
            }
        } catch (Exception e) {
            XLogger.warn("Ldap user[{}] query throws:\n", uid, XStrings.getStackTrace(e));
        }
        return userDN.toString();
    }

    /**
     * 通过uid获取dn,然后连接验证
     */
    public boolean validate(String uid, String password) {
        try {
            if(ctx != null) {
                String userDN = queryUserDN(uid);
                ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
                ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
                ctx.reconnect(null);
                return true;
            }
        } catch (NamingException e) {
            XLogger.warn("Ldap user[{}] validate failed cause:\n{}", uid, XStrings.getStackTrace(e));
        } finally {
            close();
        }
        return false;
    }

}
