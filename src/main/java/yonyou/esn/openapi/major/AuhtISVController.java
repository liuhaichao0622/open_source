package yonyou.esn.openapi.major;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import yonyou.esn.openapi.util.MapUtil;

import com.alibaba.fastjson.JSONObject;

/**isv授权Demo
 * @author liuhaichao
 *
 */
@Controller
@RequestMapping("yyisv")
public class AuhtISVController {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuhtISVController.class);
	
	@Autowired
	private IAuthAppSuites authAppSuites;
	
	/**
	 * 将定时推送的Ticket存储本地
	 */
	private static String LOCAL_TICKET;
	
	/**
	 * 套件令牌
	 */
	private static String SUITE_ACCESS_TOKEN;
	
	/**
	 * 点击授权时,将推送的临时授权码存储在本地
	 */
	private static String TEMP_CODE;
	
	/**
	 * 永久授权码
	 */
	private static String PERMANENT_CODE;
	
	/**
	 * 访问接口的通行证
	 */
	private static String ACCESS_TOKEN;
	
	@Value("${isv_auth_suite_key}")
	private String suiteKey;
	
	@Value("${isv_auth_suite_secret}")
	private String suiteSecret;
	
	@Value("${isv_auth_aes_key}")
	private String aesKey;
	
	@Value("${isv_auth_token}")
	private String token;
	
	
	/**
	 * 模拟服务端接收ticket
	 * @param req
	 * @param msg_signature 签名
	 * @param timeStamp 时间戳
	 * @param nonce 随机数
	 * @param encrypt 加密数据
	 * @return
	 */
	@RequestMapping(value = "ticket",produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getAndHandleTicket(HttpServletRequest req,@RequestParam("encrypt") String encrypt,
			@RequestParam("msg_signature") String msg_signature,@RequestParam("timestamp") String timestamp,
			@RequestParam("nonce") String nonce, HttpServletResponse response){
		String xmlString = authAppSuites.decodeData(msg_signature,timestamp, nonce, encrypt,
				token, aesKey, suiteKey);
		Map<String,String> xmlMap = MapUtil.xmlToMap(xmlString);
		String ticketType = xmlMap.get("InfoType");
		LOG.info("此次接收的ticket类型是="+ticketType);
		if("suite_ticket".equals(ticketType)){
			//从推送的ticket中解析处suiteTicket
			String suiteTicket = xmlMap.get("SuiteTicket");
			LOCAL_TICKET = suiteTicket;
			LOG.info("成功接收SuiteTicket="+LOCAL_TICKET);
			//每次都从最新的ticket中获取suite_access_token
			String suite_access_token = authAppSuites.getSuiteAccessToken(suiteKey,suiteSecret,LOCAL_TICKET);
			SUITE_ACCESS_TOKEN = suite_access_token;
			LOG.info("suite_access_token="+suite_access_token);
		}else if("authorized".equals(ticketType)){
			//从ticket中解析出tempCode
			String tempCode = xmlMap.get("AuthorizationCode");
			TEMP_CODE = tempCode;
			LOG.info("成功接收临时授权码="+tempCode);
			
			//如果推送的是临时授权码则进行授权动作
			//授权过程:   suite_token-->临时授权码-->永久授权码
			//suiteKey+suiteSecret+suiteTicket --> suiteToken
			//suiteToken+suiteKey+tempCode --> permanentCode
			String permanent_code = authAppSuites.getPermanentCode(SUITE_ACCESS_TOKEN,suiteKey,TEMP_CODE);
			LOG.info("永久授权码="+permanent_code);
			PERMANENT_CODE = permanent_code;
		}
		response.setStatus(200);
		return "";
	}
	
	/**
	 * 获取授权空间的接口访问令牌(access_token:),即第三方开发者拿此访问空间接口
	 */
	@RequestMapping(value = "/getaccess_token")
	@ResponseBody
	public String getAccessToken(){
		ACCESS_TOKEN = authAppSuites.getAccessToken(SUITE_ACCESS_TOKEN,suiteKey,PERMANENT_CODE);
		Map<String,String> map = new HashMap<String, String>();
		map.put("ticket", LOCAL_TICKET);
		map.put("tempCode", TEMP_CODE);
		map.put("permanentCode", PERMANENT_CODE);
		map.put("accessToken", ACCESS_TOKEN);
		return JSONObject.toJSONString(map);
	}
	
}
