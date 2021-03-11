package dev.xframe.admin.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Identity;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import dev.xframe.utils.XLogger;
import dev.xframe.utils.XStrings;

/**
 * @author luzj
 */
public class ShellExecs {
    
    public static class Local {
        public static String exec(String cmd) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(cmd);
                int status = process.waitFor();
                return status == 0 ? 
                        readFrom(process.getInputStream()): 
                        readFrom(process.getErrorStream());
            } catch (IOException | InterruptedException e) {
                return XStrings.getStackTrace(e);
            } finally {
                if(process != null) process.destroy();
            }
        }
    }
    
    public static class Remote {
        public static String exec(String host, String privateKey, String cmd) {
            return SSH2.exec(host, null, privateKey, cmd);
        }
        public static String exec(String host, String username, String password, String cmd) {
            return SSH2.exec(host, username, password, cmd);
        }
    }
    
    static String readFrom(InputStream in) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int b;
        while((b = in.read()) != -1) {
            buf.write(b);
        }
        return buf.toString();
    }
    
    //ssh2
    static UserInfo Empty = new UserInfo() {
        public String getPassphrase() {return null;}
        public String getPassword() {return null;}
        public boolean promptPassphrase(final String arg0) {return true;}
        public boolean promptPassword(final String arg0) {return true;}
        public boolean promptYesNo(final String arg0) {return true;}
        public void showMessage(final String arg0) {}
    };
    
    static final String RSAIdentityName = "xframe";
    static class RSAIdentity implements Identity {
        KeyPair kpair;
        public RSAIdentity(JSch jsch, String key) {
            try {
                kpair = KeyPair.load(jsch, key.getBytes(), null);
            } catch (JSchException e) {
                XLogger.error("SSH2.exec()", e);
            }
        }
        public boolean setPassphrase(byte[] passphrase) throws JSchException {
            return kpair.decrypt(passphrase);
        }
        public byte[] getPublicKeyBlob() {
            return kpair.getPublicKeyBlob();
        }
        public byte[] getSignature(byte[] data) {
            return kpair.getSignature(data);
        }
        public boolean decrypt() {
            return true;
        }
        public String getAlgName() {
            return "ssh-rsa";
        }
        public String getName() {
            return RSAIdentityName;
        }
        public boolean isEncrypted() {
            return kpair.isEncrypted();
        }
        public void clear() {
            //do nothing
        }
    }
    static class SSH2 {
        static JSch jsch = new JSch();
        static String exec(final String host, final String username, final String password, final String command) {
            Session session = null;
            Channel channel = null;
            try {
                session = jsch.getSession(username, host, 22);
                //ssh-rsa  PrivateKey
                if(password.startsWith("-----") &&
                        !jsch.getIdentityNames().contains(RSAIdentityName)) {
                    jsch.addIdentity(new RSAIdentity(jsch, password), null);
                } else {
                    session.setPassword(password);
                }
                
                session.setUserInfo(Empty);
                session.connect();
                
                channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);
                
                channel.setInputStream(null);
                InputStream in = channel.getInputStream();
                
                channel.connect();
                
                return readFrom(in);
            } catch (Throwable t) {
                XLogger.error("SSH2.exec()", t);
                return t.getMessage();
            } finally {
                if(channel != null) channel.disconnect();
                if(session != null) session.disconnect();
            }
        }
    }
    
}
