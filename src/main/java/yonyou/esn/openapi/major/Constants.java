package yonyou.esn.openapi.major;

/**存储一些授权套件用的常量(以后存储于properties配置文件)
 * @author liuhaichao
 *
 */
public class Constants {
	
	/**
	 * 套件Key(亦套件Id)
	 */
	public static String SUITE_KEY = "b0c81df0-65e3-4c4d-9133-4444b4823e89";
	
	/**
	 * 套件Secret
	 */
	public static String SUITE_SECRET = "82ebaa3e-1534-4984-97e3-6fc9534d11f0";
	
	/**
	 * 加解密参数
	 */
	public static String AES_KEY = "0123456789012345678901234567890123456789012";
	
	/**
	 * 用于解密
	 */
	public static String TOKEN = "tokentokentokentokentoken";
	
	/**
	 * 请求授权服务器的根路径 (openapi server)
	 */
//	public static String AUTH_BASE_PATH = "http://10.2.104.22:8888/open-api";//本机
//	public static String AUTH_BASE_PATH = "http://172.20.19.200:10355/openapi";//本地测试服务器
	public static String AUTH_BASE_PATH = "http://121.42.30.191:8092/openapi";//阿里云测试服务器
	
	/**
	 * 手动推送服务的根路径 (operation server)
	 */
//	public static String PUSH_BASE_PATH = "http://10.2.104.22:8080/operation-server";
	public static String PUSH_BASE_PATH = "http://172.20.19.200:10101";
}
