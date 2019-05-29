package lib.doldory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashLogger {
	
	public static long LIMIT_SIZE = 1024 * 1024 * 3;
	private static final SimpleDateFormat now = new SimpleDateFormat("[yyyy-MM-DD HH:mm:ss]-");
	
	private static String BOUNDARY = "^----^";
	private static String LINE_FEED = "\r\n";
	public static String CHARSET = "UTF-8";

	public static void save(String filename, Throwable throwable, boolean appendFlag) {
		final String _filename = filename;
		final Throwable _throwable = throwable;
		final boolean _appendFlag = appendFlag;
		
		new Thread(new Runnable() {
			
			public void run() {

				synchronized (_filename) {
					PrintWriter pw = null;
					BufferedWriter bw = null;
					FileWriter fw = null;
					
					try {
						File file = new File(_filename);
						if (!file.exists()) {
							if (!file.getParentFile().isDirectory())
								file.getParentFile().mkdirs();
							file.createNewFile();
							
						} else {
							if (file.length() > LIMIT_SIZE) {
								file.delete();
								file.createNewFile();
							}
						}
						pw = new PrintWriter(new BufferedWriter(new FileWriter(_filename, _appendFlag)), _appendFlag);
						pw.write(now.format(new Date()));
						_throwable.printStackTrace(pw);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (pw != null) pw.close();
							if (bw != null) pw.close();
							if (fw != null) fw.close();
						} catch (Exception e) {};
					}
				}
				
			}
		}).start();
		
	}
	
	public static void save(String filename, String message, boolean appendFlag) {
		final String _filename = filename;
		final String _message = message;
		final boolean _appendFlag = appendFlag;
		
		new Thread(new Runnable() {
			
			public void run() {

				synchronized (_filename) {
					PrintWriter pw = null;
					BufferedWriter bw = null;
					FileWriter fw = null;
					
					try {
						File file = new File(_filename);
						if (!file.exists()) {
							if (!file.getParentFile().isDirectory())
								file.getParentFile().mkdirs();
							file.createNewFile();
							
						} else {
							if (file.length() > LIMIT_SIZE) {
								file.delete();
								file.createNewFile();
							}
						}
						pw = new PrintWriter(new BufferedWriter(new FileWriter(_filename, _appendFlag)), _appendFlag);
						pw.write(now.format(new Date()));
						pw.write(_message);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (pw != null) pw.close();
							if (bw != null) pw.close();
							if (fw != null) fw.close();
						} catch (Exception e) {};
					}
				}
				
			}
		}).start();
		
	}
	
	public static String send(String url, String filename) {
		String result = "";
		OutputStream os = null;
		PrintWriter pw = null;
		FileInputStream fis = null;
		BufferedReader br = null;
		
		try {
			File file = new File(filename);
			
			URL _url = new URL(url);
			HttpURLConnection con = (HttpURLConnection) _url.openConnection();
			con.setRequestProperty("Content-Type", "multipart/form-data;charset="+CHARSET+";boundary="+BOUNDARY);
			con.setRequestMethod("POST");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(30*1000);
			os = con.getOutputStream();
			pw = new PrintWriter(new OutputStreamWriter(os, CHARSET), true);
			pw.append("--" + BOUNDARY).append(LINE_FEED);
			pw.append("Content-Disposition: form-data; name=\"file\"; filename=\""+file.getName()+"\"").append(LINE_FEED);
			pw.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(LINE_FEED);
			pw.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
			pw.append(LINE_FEED);
			pw.flush();
			
			fis = new FileInputStream(file);
			byte[] buffer = new byte[1024*4];
			int readCnt = -1;
			while((readCnt = fis.read(buffer)) != -1)
				os.write(buffer, 0, readCnt);
			os.flush();
			pw.append(LINE_FEED);
			pw.append("--" + BOUNDARY + "--").append(LINE_FEED);
			pw.flush();
			pw.close();
			
			int resCode = con.getResponseCode();
			if (resCode == HttpURLConnection.HTTP_OK || resCode == HttpURLConnection.HTTP_CREATED) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String readLine;
				StringBuffer sb = new StringBuffer();
				while ((readLine = br.readLine()) != null)
					sb.append(readLine);
				br.close();
				result = sb.toString();
			} else {
				result = "Error: " + resCode;
			}
			 
		} catch (Exception e) {
			e.printStackTrace();
			result = "Exception: " + e.getMessage();
		} finally {
			try {
				if (fis != null) fis.close();
				if (pw != null) pw.close();
				if (br != null) br.close();
			} catch (IOException e) {}
		}
		return result;
	}
	
	public static void asyncSend(String url, String filename, final Callback callback) {
		final String _url = url;
		final String _filename = filename;
		
		new Thread(new Runnable() {
			
			public void run() {
				String result = send(_url, _filename);
				if (callback != null) {
					callback.call(result);
				}
			}
		}).start();
	}
	
	interface Callback {
		public void call(String response);
	}
}
