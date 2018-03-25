package com.xr.safe360.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5Util {

	/**
	 * md5加密字符串
	 * @param psd
	 * @return
	 */
	public static String encoder(String psd) {
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");
			psd =psd +"mobilesafe";
			byte[] bs = instance.digest(psd.getBytes());
			StringBuffer stringBuffer = new StringBuffer();
			for (byte b : bs) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);
				if(hexString.length()<2) {
					hexString="0"+hexString;
				}
				stringBuffer.append(hexString);
			}
			return stringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("加密失败");
		}

	}

}
