package com.usaparel.vpview.servlets;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.sql.Connection;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;


@WebServlet(name = "ReportServlet", urlPatterns = { "/reportservlet" })  
public class PDFServlet extends HttpServlet {  
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";  
  
  
    public void init(ServletConfig config) throws ServletException {  
        super.init(config);  
    }  
      
    protected Connection getConnection() {  
          
       
        
        Connection conn = null;      
           
      
        return conn;  
    }  
  
  
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  
        
        response.setContentType(CONTENT_TYPE);  
        Map parameters = new HashMap();  
              
                
               InputStream is = null;  
               try  
               {  
                 is = getServletContext().getResourceAsStream("/reports/new.jasper");  
                   
                
          
                   if (is == null){
                       System.out.println("is is null");
                   }
                 response.setContentType("application/pdf");  
                 response.addHeader("Content-Disposition", "attachment; filename=ClassDataReport.pdf");  
                    
                 JasperReport jasperReport = (JasperReport) JRLoader.loadObject(is);
                  
                   
                   InitialContext initialContext = new InitialContext();
                   DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/connDS");
                   System.out.println("here......");
                   JasperPrint jasperPrint = null;
                   if (jasperReport != null && parameters != null && ds.getConnection() != null){
                       
                       jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,ds.getConnection());
                       //JasperViewer.viewReport(jasperPrint, false);
                       JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream()); 
                   }
                       
                  
                
                  
                            
               }  
               catch (Exception e) {  
                 e.printStackTrace();  
               } finally {  
                  
                 try {  
                     if(is !=null){  
                         is.close();  
                     }                     
                      
                 } catch (Exception localException1)  
                 {  
                 }  
               }  
          
        PrintWriter out = response.getWriter();  
        out.println("<html>");  
        out.println("<head><title>ReportServlet</title></head>");  
        out.println("<body>");  
        out.println("<p>The servlet has received a GET. This is the reply.</p>");  
        out.println("</body></html>");  
        out.close();  
    }  
      
    protected static void closeConnection(Connection conn) {  
        try {  
            if(conn !=null){  
                conn.close();  
            }             
        } catch (Exception ex) {  
            //  System.out.println("Developer Msg : Exception in printReport1Servlet.closeConnection()");  
        }  
    }  
}  