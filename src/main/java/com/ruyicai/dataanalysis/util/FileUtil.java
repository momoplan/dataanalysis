package com.ruyicai.dataanalysis.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static String read(String filename) {
		StringBuilder sb = new StringBuilder();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filename));
			String data = null;
			while (null != (data = in.readLine())) {
				sb.append(data);
			}
		} catch (Exception e) {
			logger.error("读取文件出错, filename:　" + filename, e);
			throw new RuntimeException("读取文件出错, filename:　" + filename, e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		return sb.toString();
	}

	public static void write(String filepath, String filename, String content) {
		try {
			File parent = new File(filepath);
			if (!parent.exists()) {
				parent.mkdirs();
			}
			File file = new File(filepath, filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file)));
			bufferedWriter.write(content);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (Exception e) {
			logger.error("创建文件出错", e);
			throw new RuntimeException("创建文件出错, filename:　" + filename
					+ ", error: " + e.getMessage());
		}
	}

	public static Properties loadProps(String resourceLocation) {
		Properties props = new Properties();
		try {
			props.load(FileUtil.class.getClassLoader().getResourceAsStream(
					resourceLocation));
		} catch (IOException e) {
			logger.error("load properties error", e);
		}
		return props;
	}
}
