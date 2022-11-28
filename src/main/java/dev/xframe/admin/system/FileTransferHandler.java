package dev.xframe.admin.system;

import dev.xframe.inject.Bean;
import dev.xframe.inject.Providable;
import dev.xframe.utils.XCaught;
import dev.xframe.utils.XProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 文件上传下载
 * @author luzj
 */
@Bean
@Providable
public class FileTransferHandler {

    String storeDir = XProperties.get("store.dir", XProperties.get("work.dir", XProperties.get("user.dir")));

    public File upload(String originName, File tmpFile) {
        try {
            Path target = Files.move(tmpFile.toPath(), Paths.get(storeDir, originName), StandardCopyOption.ATOMIC_MOVE);
            return target.toFile();
        } catch (IOException e) {
            throw XCaught.throwException(e);
        }
    }

    public File preview(String name) {
        return Paths.get(storeDir, name).toFile();
    }

}
