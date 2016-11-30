package yonyou.esn.openapi.entrance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;

/**文件功能相关
 * @author liuhaichao
 *
 */
@Controller
//@RequestMapping("file")
public class FileController implements ServletContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(FileController.class);
	
	//Spring这里是通过实现ServletContextAware接口来注入ServletContext对象  
    private ServletContext servletContext;
	
    @RequestMapping("file/download")  
    public void fileDownload(HttpServletResponse response){  
    	/**
    	 * 1.设置文件ContentType类型
		   2.设置文件头
		   3.通过response获取ServletOutputStream对象(out)
		   4.写到输出流(out)中
    	 */
        //获取网站部署路径(通过ServletContext对象)，用于确定下载文件位置，从而实现下载  
        String path = servletContext.getRealPath("/");  
  
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型  
        response.setContentType("multipart/form-data");  
        //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)  
        response.setHeader("Content-Disposition", "attachment;fileName="+"openapi-auth-demo.war");  
//        ServletOutputStream out;  
        //通过文件路径获得File对象(假如此路径中有一个download.pdf文件)  
        File file = new File(path + "download/" + "openapi-auth-demo.war");  
  
        try {  
//            FileInputStream inputStream = new FileInputStream(file);  
//  
//            //3.通过response获取ServletOutputStream对象(out)  
//            out = response.getOutputStream();  
//  
//            int b = 0;  
//            byte[] buffer = new byte[512];  //这里老报错"数组越界"(除非new byte[1])
//            while (b != -1){  
//                b = inputStream.read(buffer);  
//                //4.写到输出流(out)中  
//                out.write(buffer,0,b);  
//            }  
        	
        	//读取文件  
            InputStream inputStream = new FileInputStream(file);  
            OutputStream out = response.getOutputStream();  
            //写文件  
            int b;  
            while((b=inputStream.read())!= -1)  
            {  
                out.write(b);  
            } 
            inputStream.close();  
            out.close();  
            out.flush();  
  
        } catch (IOException e) {  
            LOG.error("下载文件{}失败!","openapi-auth-demo.war",e);  
        }  
    } 
    
	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		 this.servletContext = servletContext;
	}

}
