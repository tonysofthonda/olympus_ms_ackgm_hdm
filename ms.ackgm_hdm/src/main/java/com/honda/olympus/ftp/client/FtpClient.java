package com.honda.olympus.ftp.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;

import com.honda.olympus.service.LogEventService;
import com.honda.olympus.utils.MonitorConstants;
import com.honda.olympus.vo.EventVO;

public class FtpClient {

	private String server;

	private Integer port;

	private String user;

	private String password;

	private String workDir;

	private FTPClient ftp;
	
	public FtpClient(String server, Integer port, String user, String password, String workDir) {
		super();
		this.server = server;
		this.port = port;
		this.user = user;
		this.password = password;
		this.workDir = workDir;
	}

	public void connect() throws IOException {
		ftp = new FTPClient();

		
		System.out.println("Connection FTP server: " + server);

		// System.out.println("port: " + port);
		// System.out.println("user: " + user);
		System.out.println("workDir: " + workDir);

		ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

		ftp.connect(server, port);
		// ftp.connect(server);

		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new IOException("Exception in connecting to FTP Server");
		}

		ftp.enterLocalPassiveMode();

		ftp.login(user, password);

	}

	public boolean listFiles() throws IOException {
		FTPFile[] files = ftp.listFiles(workDir);

		if (files.length != 0) {

			Arrays.stream(files).forEach(f -> System.out.println("Files: " + f.getName()));
			return Boolean.TRUE;

		} else {
			return Boolean.FALSE;
		}
	}

	public boolean listDirectories() throws IOException {
		FTPFile[] files = ftp.listDirectories("/");

		if (files.length != 0) {

			Arrays.stream(files).forEach(f -> System.out.println("Directories: " + f.getName()));
			return Boolean.TRUE;

		} else {
			return Boolean.FALSE;
		}
	}

	public FTPFile listFirstFile(String serviceName) throws IOException {
		FTPFile[] files = ftp.listFiles(workDir);

		Optional<FTPFile> ftpFile = Arrays.stream(files).findFirst();

		
		if (ftpFile.isPresent()) {
			return ftpFile.get();
		} else {
			
			return null;
		}  

	}

	public void close() throws IOException {
		ftp.disconnect();
	}
}
