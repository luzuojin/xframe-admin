package dev.xframe.admin.system;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import dev.xframe.admin.conf.SysProperties;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Providable;
import dev.xframe.utils.XCaught;

/**
 * 文件上传下载
 * @author luzj
 */
@Bean
@Providable
public class FileTransferHandler {
	
	public File upload(String originName, File tmpFile) {
		try {
			Path target = Files.move(tmpFile.toPath(), Paths.get(SysProperties.getStoreDir(), originName), StandardCopyOption.ATOMIC_MOVE);
			return target.toFile();
		} catch (IOException e) {
			return XCaught.throwException(e);
		}
	}
	
	public File preview(String name) {
		return Paths.get(SysProperties.getStoreDir(), name).toFile();
	}
	

}
