package com.pw.services;
//本类为银联网关接口的java Demo类

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pw.security.Base64;
import com.pw.security.ByteUtils;
import com.pw.util.HttpClientUtil;
import com.pw.util.QRCodeImg;

public class QrCode {
		
		
		//根据时间自动生成新的订单号
		public static String merchantTradeId() {
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String merchantTradeId = "TEST"+df.format(date);
			return merchantTradeId;
		}
		
		//设置并组装参数，用于对参数进行签名 ，在这个组装的map对象中不含 signType  和 sign 字段，
		public static  Map<String , String> paramMap(){
			String app_id = "752";                                 
			String currency = "CNY";							 
			String amount = "10.00";                              
			String order_no = merchantTradeId();				 
			String terminal_no = "";   //传空值				 
			String payment_channel = "WECHAT";            //微信       //UNIONPAY 银联   ALIPAY  支付宝
			
			
			Map<String , String> paramMap = new HashMap<String , String>();
			paramMap.put("app_id", app_id); 
			paramMap.put("currency", currency); 
			paramMap.put("amount", amount);
			paramMap.put("order_no", order_no);
			paramMap.put("terminal_no", terminal_no);
			paramMap.put("payment_channel", payment_channel);
			return paramMap;
		}
		
		
		
		
		public static void service(HttpServletRequest request, HttpServletResponse response,String geturl) throws ServletException,
		IOException {
				ServletOutputStream stream = response.getOutputStream();
				String url = URLDecoder.decode(geturl);
			
				int width = 300;// 图片的宽度
				int height = 300;// 高度
				
				QRCodeWriter writer = new QRCodeWriter();
				BitMatrix m;
				try {
					m = writer.encode(url.toString(), BarcodeFormat.QR_CODE, height, width);
					MatrixToImageWriter.writeToStream(m, "png", stream);
				} catch (WriterException e) {
					e.printStackTrace();
				} finally {
					if (stream != null) {
						stream.flush();
						stream.close();
					}
				}
}
		 
		 /**
		  * 合并上述方法，发送请求
		  */
		 public static void main(String[] args) {
			 	String input_charset = "UTF-8";
				String requestUrl = "http://test-nms.nova2pay.com/payment/otoSoft/v3/getQrCode.html"; //银联网关请求地址
				String PrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALVhKEW/vMjKTa00r3P4/6UWOwP3qECxKSf7WBKEuySdFsF/So5BysLbNwto3O99cxbuSDz/86E7/WoA+d5NsHwl6hi2ps5wH0E0rN4XogW4ZvzIKk/BFxLT2zByUqO6SGRkV0noXHUHRqtsSEzdf5l7frsSsdcYT76WjWiZO8ApAgMBAAECgYAsjXr49woGQDsoSoK2d/lDsdw5M0Iu8dVsLr8JfXUCn1uRmBTWMK7/gh1ZPh7W7PeyMEGqSiyr9DJhMXAu/OJWkorN2g+tWn7ZYVHqtwoWLbHtbTZCLHHPiGvWSYDRKRrHN46ibl3cT+yxHgODT0+b7yr3gQzJaoHQJqYNZEFsoQJBAN2G6kQSQHMxHMWdile08G5WyaOtUqdnW72mRflHIzt6fcIoBuyMkdUYP5SHBMPvKvkE12KDPJ9tRv80aTvczgUCQQDRmt22VTU/r6Wn1+PgKfBd8JlxFIS03TqQ02PR9GabbhEWj+r2dtJ4S7qlFE3PyxerTLcg/Y1pWBqmKmb7LN7VAkEAksqKw46gnHQnz58EA/hG9aaWuNnqEjnAJdxfM756zzfsy1JAvFCtddo6j04kzzzVaetWWdYCvtXnnMZ7EQrQjQJANmFSglqq/QHqHZiyY1ceKJEijib/oxj+d9KQREl/UXYF8u+VyynawyEKpIeXBIExe9zkaie+R4V3LIM1wpbKnQJBALsqiQX9MOemmo9iSAnLKT2G+S2P4H1uJY9eA/kdMaTW5s9bUKSvhxdc+DtTPi5WwgphrBzDaer66uAk+SnzfpE=";
				String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDmwwFxxkClvsBzZUtE0CN4S7P0QWZxnpxn2De0zlqbjY6Put/8738SXYkGsuBIb5QZU3tDb/0hmON3zQ84BLexksP2iNqY1q1VSeY2NkV/QxrCUefUedTFsDU+ZcIB5JJ02m4fqpYtzYowtf5JrgjYHcyrO1IaX3NVITm9EPOMHQIDAQAB";
				HttpServletRequest request = null;
	        	HttpServletResponse response = null;
			   //将 请求的参数中 除了sign和signType外的参数组装成map对象
	        	try {
				     Map<String , String> paramMap = paramMap();   
				     System.out.println("参与字符串拼接的map："+paramMap);
				     String result = HttpClientUtil.sendPostInfo(paramMap, requestUrl, "utf-8");
				     System.out.println(result);
		        	 JSONObject jsonResult= JSON.parseObject(result);
		        	JSONObject jsonData=(JSONObject) jsonResult.get("data");
		        	System.out.println("需要用这个链接生成一个二维码图片(生成方法如上【service】方法所示)："+jsonData.get("qrUrl"));
	        	}catch (Exception e) {
					e.printStackTrace();
				}
		}
}
