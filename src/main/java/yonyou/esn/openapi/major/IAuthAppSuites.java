package yonyou.esn.openapi.major;

/**
 * @author liuhaichao
 *
 */
public interface IAuthAppSuites {
	
	/**
	 * 解密密文
	 * @param encript
	 * @return
	 */
	public String decodeData(String msgSignature,String timeStamp,String nonce,String postData, String token,
			String aesKey, String suiteKey);
	
	/**
	 * 获取suite_access_token
	 * @param sUITE_KEY
	 * @param sUITE_SECRET
	 * @param suiteTicket
	 * @return
	 */
	public String getSuiteAccessToken(String suiteKey, String suiteSecret,
			String suiteTicket);
	
	/**
	 * 获取永久授权码
	 * @param suite_access_token
	 * @param sUITE_KEY
	 * @param pre_auth_code
	 * @return
	 */
	public String getPermanentCode(String suiteAccessToken, String suiteKey,
			String pre_auth_code);
	
	/**
	 * 获取空间访问令牌(access_token)
	 * @param pernanentCode
	 * @return
	 */
	public String getAccessToken(String suiteAccessToken,String suiteKey,String pernanentCode);
	
}
