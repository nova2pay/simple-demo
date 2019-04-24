package com.nova2pay.test.pccTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class DistributeRSADemo {
	/**
	 * 加密方法名
	 */
	public static final String KEY_ALGORITHM = "RSA";

	public static final String SIGNATURE_ALGORITHM = "SHA1WithRSA";
	
	public final static String Detail_List="detailList";
	
	public static void main(String[] args) throws Exception {
		String url="https://api.fpglink.com/v2/distribute/withdraw.html";

		//正式环境 测试商户号停用
		String merchantId="752";
		long time=System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String batchNo="testBatchNo"+merchantId+time;
		String serialNo="testSerialNo"+merchantId+time;
		String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALVhKEW/vMjKTa00r3P4/6UWOwP3qECxKSf7WBKEuySdFsF/So5BysLbNwto3O99cxbuSDz/86E7/WoA+d5NsHwl6hi2ps5wH0E0rN4XogW4ZvzIKk/BFxLT2zByUqO6SGRkV0noXHUHRqtsSEzdf5l7frsSsdcYT76WjWiZO8ApAgMBAAECgYAsjXr49woGQDsoSoK2d/lDsdw5M0Iu8dVsLr8JfXUCn1uRmBTWMK7/gh1ZPh7W7PeyMEGqSiyr9DJhMXAu/OJWkorN2g+tWn7ZYVHqtwoWLbHtbTZCLHHPiGvWSYDRKRrHN46ibl3cT+yxHgODT0+b7yr3gQzJaoHQJqYNZEFsoQJBAN2G6kQSQHMxHMWdile08G5WyaOtUqdnW72mRflHIzt6fcIoBuyMkdUYP5SHBMPvKvkE12KDPJ9tRv80aTvczgUCQQDRmt22VTU/r6Wn1+PgKfBd8JlxFIS03TqQ02PR9GabbhEWj+r2dtJ4S7qlFE3PyxerTLcg/Y1pWBqmKmb7LN7VAkEAksqKw46gnHQnz58EA/hG9aaWuNnqEjnAJdxfM756zzfsy1JAvFCtddo6j04kzzzVaetWWdYCvtXnnMZ7EQrQjQJANmFSglqq/QHqHZiyY1ceKJEijib/oxj+d9KQREl/UXYF8u+VyynawyEKpIeXBIExe9zkaie+R4V3LIM1wpbKnQJBALsqiQX9MOemmo9iSAnLKT2G+S2P4H1uJY9eA/kdMaTW5s9bUKSvhxdc+DtTPi5WwgphrBzDaer66uAk+SnzfpE=";
		Map<String,String> paramMap =new HashMap<String,String>();
		paramMap.put("batchNo", batchNo);
		paramMap.put("batchRecord", "2");
		paramMap.put("currencyCode", "CNY");
		paramMap.put("merchantId", merchantId);
		paramMap.put("totalAmount", "2.00");
		paramMap.put("isWithdrawNow", "1");
		paramMap.put("payDate", sdf.format(new Date()));
		//call back for th url, message type  JSON, return Message the same with  queryWithdraw  in doc
		//paramMap.put("notifyUrl", "https://nms.nova2pay.com/v2/distribute/testNotify.html");
		paramMap.put("signType", "RSA");
	
		List<Map<String,String>> detailList=new ArrayList<Map<String,String>>();
		Map<String,String> detailInfo1=new HashMap<String, String>();
		detailInfo1.put("amount", "1.00");
		detailInfo1.put("accountType", "储蓄卡");
		detailInfo1.put("receiveType", "个人");
		detailInfo1.put("bankName", "BOC");
		detailInfo1.put("serialNo", serialNo+"1");
		detailInfo1.put("receiveName", "张三");
		detailInfo1.put("bankNo", "6216690800000710925");
		
		Map<String,String> detailInfo2=new HashMap<String, String>();
		detailInfo2.put("amount", "1.00");
		detailInfo2.put("accountType", "储蓄卡");
		detailInfo2.put("receiveType", "个人");
		detailInfo2.put("bankName", "BOC");
		detailInfo2.put("serialNo", serialNo+"2");
		detailInfo2.put("receiveName", "李四");
		detailInfo2.put("bankNo", "6216690800000710925");
		
		detailList.add(detailInfo1);
		detailList.add(detailInfo2);
		//no need for sign
		JSON.toJSONString(detailList);

		
		Map<String, String> signMap = addRequestSignInMap(paramMap, privateKey);
		System.out.println(signMap);
		//no need for sign
		signMap.put(Detail_List, JSON.toJSONString(detailList));
		
		String returnMsg = sendJson(url, JSON.toJSONString(signMap));
		System.out.println("returnMsg====="+returnMsg);
	}
	
	
	
	public static String sendJson(String urlstr,String query){
		String resp= null;
		JSONObject json= null;
        System.out.println("发送到URL的报文为："+query);
        try {
            URL url = new URL(urlstr); //url地址

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type","application/json");
            connection.connect();

            try (OutputStream os = connection.getOutputStream()) {
                os.write(query.getBytes("UTF-8"));
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String lines;
                StringBuffer sbf = new StringBuffer();
                while ((lines = reader.readLine()) != null) {
                    lines = new String(lines.getBytes(), "utf-8");
                    sbf.append(lines);
                }
                System.out.println("返回来的报文："+sbf.toString());
                resp = sbf.toString();    
                json = (JSONObject)JSON.parse(resp);
            }
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }      
        if(json!=null){
        	return json.toJSONString();
        }
        return "";
	}
	
	
	public static Map<String, String> addRequestSignInMap(Map<String, String> map, String privateKey) throws Exception {
		Map<String, String> signMap = new HashMap<String, String>();
		String[] unSignKey =new String[] { "sign", "signType", Detail_List };
		// 16进制签名转10进制签名
		String serverSign = "";
		try {
			String signType = map.get("signType");
			signMap = paraFilter(map, unSignKey);
			String preSignStr = createLinkString(signMap);
			System.out.println("serverSign Before String:" + preSignStr);
			if ("RSA".equalsIgnoreCase(signType)) {
				serverSign =signToHexAscii(privateKey, preSignStr, "UTF-8");
			}
			System.out.println("serverSign Result:" + serverSign);
			signMap.put("signType", signType);
			signMap.put("sign", serverSign);
		} catch (Exception e) {
			System.out.println("下发结果生成签名异常");
		}
		return signMap;
	}
	
	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			// 拼接时，不包括最后一个&字符
			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}
	
	/**
	 * 除去数组中的空值和签名参数
	 * 
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray, String[] params) {

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}
		// break 打断当前循环语句 执行后续代码 continue 跳出当前循环
		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("") || value.equals("null") || existParams(key, params)) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}
	/**
	 * 该参数是否在给定数组中存在
	 * 
	 * @param params
	 * @param key
	 */
	private static boolean existParams(String key, String[] params) {
		for (String param : params) {
			if (key.equalsIgnoreCase(param)) {
				return true;
			}
		}
		return false;
	}
	
	
	public static String signToHexAscii(String privateKey, String content, String charset) throws Exception {
		String oralSign=sign(privateKey, content, charset);
		System.out.println("oralSign==="+oralSign);
		return DecimalToHex(oralSign);
	}
	/**
	 * 10进制转16进制
	 * 
	 * @param str
	 * @return
	 */
	public static String DecimalToHex(String str) {
		return toHexAscii(str.getBytes());
	}
	public static String toHexAscii(byte[] bytes) {
		int len = bytes.length;
		StringWriter sw = new StringWriter(len * 2);
		for (int i = 0; i < len; ++i)
			addHexAscii(bytes[i], sw);
		return sw.toString();
	}


	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 * @return 加密后字符串
	 * @throws Exception
	 */
	public static String sign(String privateKey, String content, String charset) throws Exception {
		// 解密由base64编码的私钥
		byte[] keyBytes = Base64.decode(privateKey);

		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取私钥匙对象
		PrivateKey myPriKey = factory.generatePrivate(priPKCS8);

		// 用私钥对信息生成数字签名
		Signature signet = Signature.getInstance(SIGNATURE_ALGORITHM);
		signet.initSign(myPriKey);
		signet.update(content.trim().getBytes(charset));

		return Base64.encode(signet.sign());
	}


	public static void addHexAscii(byte b, StringWriter sw) {
		int ub = unsignedPromote(b);
		int h1 = ub / 16;
		int h2 = ub % 16;
		sw.write(toHexDigit(h1));
		sw.write(toHexDigit(h2));
	}
	private static char toHexDigit(int h) {
		char out;
		if (h <= 9)
			out = (char) (h + 0x30);
		else
			out = (char) (h + 0x37);
		return out;
	}

	public static int unsignedPromote(byte b) {
		return b & 0xff;
	}


}
