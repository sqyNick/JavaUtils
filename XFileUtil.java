package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class XFileUtil {
	/*复制单个文件
	 * @param a 将复制文件
	 * @param b 生产文件
	 */
	public static void copyAtoB(File a,File b) throws IOException{
		if(a == null || !a.exists()){
			return;
		}
		InputStream in = null;
		FileOutputStream out = null;
		in = new FileInputStream(a);
		out = new FileOutputStream(b);
		byte[] buffer = new byte[1024];
		int byteRead = 0;
		while((byteRead = in.read(buffer)) != -1){
			out.write(buffer, 0, byteRead);
		}
		in.close();
		out.close();
	}
	/*
	 * @param aDir 要复制的目录
	 * @param bDir 生成目录
	 */
	public static void copyADirToBDir(File aDir,File bDir) throws IOException{
		if(!aDir.exists() || aDir == null){
			return;
		}
		if(!bDir.exists()){
			bDir.mkdirs();
		}
		String[] file = aDir.list();
		File temp = null;
		for(int i = 0; i < file.length ;i++){
			if(!aDir.isDirectory()){//无目录结构
				temp = new File(aDir.getAbsolutePath()+file[i]);
			}else{//有目录结构
				temp = new File(aDir.getAbsolutePath()+File.separator+file[i]);
			}
			if(!temp.isDirectory()){//无目录
				FileInputStream input = new FileInputStream(temp); 
				FileOutputStream output = new FileOutputStream(bDir.getAbsolutePath() + File.separator + 
				(temp.getName()).toString()); 
				byte[] b = new byte[1024 * 5]; 
				int len; 
				while ( (len = input.read(b)) != -1) { 
					output.write(b, 0, len); 
				} 
				output.flush(); 
				output.close(); 
				input.close(); 
			}else{//有子文件
				copyADirToBDir(new File(aDir.getAbsolutePath()+File.separator+file[i]),new File(bDir.getAbsolutePath()+File.separator+file[i])); 
			}
		}
	}
	/*
	 * @return 判断两个文件是否相同 true：相同，false：不同（只判断文件内容，对文件名不做判断）
	 */
	public static boolean isAFileEqualsBFile(File a,File b){
		if(a == null || b == null){
			return false;
		}
		if(a.length() == b.length()){
			if(getFileMD5(a).equals(getFileMD5(b))){
				return true;
			}
			return false;
		}
		return false;
	}
	
	// 计算文件的 MD5 值
	public static String getFileMD5(File file) {
	    if (!file.isFile()) {
	        return null;
	    }
	    MessageDigest digest = null;
	    FileInputStream in = null;
	    byte buffer[] = new byte[8192];
	    int len;
	    try {
	        digest =MessageDigest.getInstance("MD5");
	        in = new FileInputStream(file);
	        while ((len = in.read(buffer)) != -1) {
	            digest.update(buffer, 0, len);
	        }
	        BigInteger bigInt = new BigInteger(1, digest.digest());
	        return bigInt.toString(16);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    } finally {
	        try {
	            in.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	  
	}
	
	// 计算文件的 SHA-1 值
	public static String getFileSha1(File file) {
	    if (!file.isFile()) {
	        return null;
	    }
	    MessageDigest digest = null;
	    FileInputStream in = null;
	    byte buffer[] = new byte[8192];
	    int len;
	    try {
	        digest =MessageDigest.getInstance("SHA-1");
	        in = new FileInputStream(file);
	        while ((len = in.read(buffer)) != -1) {
	            digest.update(buffer, 0, len);
	        }
	        BigInteger bigInt = new BigInteger(1, digest.digest());
	        return bigInt.toString(16);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    } finally {
	        try {
	            in.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
}
