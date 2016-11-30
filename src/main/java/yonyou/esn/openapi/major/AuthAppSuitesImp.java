package yonyou.esn.openapi.major;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import yonyou.esn.openapi.util.HttpReq;
import yonyou.esn.openapi.util.WXBizMsgCrypt;

import com.alibaba.fastjson.JSONObject;

@Service
public class AuthAppSuitesImp implements IAuthAppSuites {
	private static final Logger LOG = LoggerFactory.getLogger(AuthAppSuitesImp.class);
	
	@Value("${isv_auth_base_path}")
	private String authBasePath;
	
	public String decodeData(String msgSignature,String timeStamp,String nonce,String postData, String token,
			String aesKey, String suiteKey) {
		WXBizMsgCrypt msgCrypt = new WXBizMsgCrypt(token, aesKey, suiteKey);
		String xmlString = msgCrypt.DecryptMsg(msgSignature, timeStamp, nonce, postData);
		return xmlString;
	}

	public String getSuiteAccessToken(String sUITE_KEY, String sUITE_SECRET,
			String suiteTicket) {
		String url = authBasePath + "/get_suite_token";
		JSONObject jsonObject = new JSONObject();
    	jsonObject.put("suite_id", sUITE_KEY);
    	jsonObject.put("suite_secret", sUITE_SECRET);
    	jsonObject.put("suite_ticket", suiteTicket);
    	String back = HttpReq.postBody(url, jsonObject.toJSONString());
    	JSONObject jsonObjec1t = JSONObject.parseObject(back);
		String data = jsonObjec1t.getString("data");
		String suite_token = "";
		if(data == null){
			LOG.error("获得access_token出错,请求参数suite_id="+sUITE_KEY+",suite_secret="+sUITE_SECRET+",suite_ticket="+suiteTicket+";返回结果="+jsonObjec1t);
			return null;
		}else{
			suite_token = JSONObject.parseObject(data).getString("suite_access_token");
		}
		return suite_token;
	}

	public String getPreAuthCode(String suite_access_token, String sUITE_KEY) {
		String url = authBasePath + "/get_pre_auth_code";
		Map<String,String> map = new HashMap<String,String>();
		map.put("suite_token", suite_access_token);
		map.put("suite_id", sUITE_KEY);
    	String back = null;
		try {
			back = HttpReq.sendGet(url, map);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			e.printStackTrace();
		}
    	JSONObject jsonObjec1t = JSONObject.parseObject(back);
		String data = jsonObjec1t.getString("data");
		String preCode = JSONObject.parseObject(data).getString("pre_auth_code");
		return preCode;
	}
	
	public String getPermanentCode(String suite_access_token, String sUITE_KEY,
			String tempCode) {
		String url = authBasePath + "/get_permanent_code?suite_token="+suite_access_token;
		JSONObject jsObj = new JSONObject();
    	jsObj.put("suite_id", sUITE_KEY);
    	jsObj.put("auth_code", tempCode);
    	String backData = HttpReq.postBody(url, jsObj.toJSONString());
    	JSONObject backDataObj = JSONObject.parseObject(backData);
		String data = backDataObj.getString("data");
		String permenentCode = "";
		if(data == null){
			LOG.error("获得永久授权码失败,请求参数suite_id="+sUITE_KEY+",auth_code="+tempCode+",token="+suite_access_token+";返回结果="+backDataObj);
			return null;
		}else{
			permenentCode = JSONObject.parseObject(data).getString("permanent_code");
			String qzId = JSONObject.parseObject(data).getString("qzId");
			String qzName = JSONObject.parseObject(data).getString("qzName");
			LOG.info("空间名称="+qzName+",空间Id="+qzId+"的永久授权码="+permenentCode);
			return permenentCode;
		}
	}

	public String getAccessToken(String suiteAccessToken, String suiteKey,
			String pernanentCode) {
		String url = authBasePath + "/get_corp_token?suite_token="+suiteAccessToken;
		JSONObject jsObj = new JSONObject();
    	jsObj.put("suite_id", suiteKey);
    	jsObj.put("permanent_code", pernanentCode);
    	String backData = HttpReq.postBody(url, jsObj.toJSONString());
    	JSONObject backDataObj = JSONObject.parseObject(backData);
		String data = backDataObj.getString("data");
		String accessToken = "";
		if(data == null){
			LOG.error("获得空间访问令牌失败,请求参数suite_id="+suiteKey+",permanent_code="+pernanentCode+";返回结果="+backDataObj);
			return null;
		}else{
			accessToken = JSONObject.parseObject(data).getString("access_token");
			String expiresIn = JSONObject.parseObject(data).getString("expires_in");
			LOG.info("已获得空间访问令牌="+accessToken+",有效时间是"+(Integer.parseInt(expiresIn)/60/60)+"小时");
			return accessToken;
		}
	}

}
