package com.ruyicai.dataanalysis.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HttpUtil {
	
	private Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	
	public static final String POST = "POST";
	
	public static final String GET = "GET";
	
	public static final String UTF8 = "UTF-8";
	
	public static final String GBK = "GBK";
	
	public static final String GB2312 = "GB2312";
	
	public String downfile(String url, String encoding) {
		InputStream in = null;
		try {
			URL postUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) postUrl
					.openConnection();
			conn.setRequestProperty("User-Agent", "Internet Explorer"); 
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod(GET);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(30000);
			in = conn.getInputStream();
			return read(in, encoding);
		} catch (Exception e) {
			logger.error("下载文件出错,url:" + url, e);
		} finally {
			close(in);
		}
		return null;
	}
	
	private void close(InputStream in) {
		if(null != in) {
			try {
				in.close();
			} catch(Exception e) {
			} 
		}
	}
	
	private void close(OutputStream out) {
		if(null != out) {
			try {
				out.close();
			} catch(Exception e) {
				
			}
		}
	}
	
	public String getResponse(String url, String method, String encoding, String body) {
		OutputStream out = null;
		InputStream in = null;
		try {
			URL postUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) postUrl
					.openConnection();
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod(method);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(20000);
			conn.connect();
			out = conn.getOutputStream();
			out.write(body.getBytes());
			out.flush();
			in = conn.getInputStream();
			String result = read(in, encoding);
			return result;
		} catch (MalformedURLException e) {
			logger.error("请求地址有误, url:" + url, e);
			throw new RuntimeException(e);
		} catch (Exception e) {
			logger.error("IO错误,url:" + url + ",body:" + body, e);
			throw new RuntimeException(e);
		} finally {
			close(in);
			close(out);
		}
	}
	
	public String read(InputStream in, String encoding) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding), 1024 * 1024);
			StringBuilder builder = new StringBuilder();
			String line = null;
			while(null != (line = reader.readLine())) {
				builder.append(line);
			}
			return builder.toString();
		} catch(Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			close(in);
		}
	}
}
