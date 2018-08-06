package servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import javax.sql.DataSource;
import java.sql.Connection;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.collections.map.ReferenceMap;
import org.apache.commons.logging.LogFactory;

@WebServlet(name = "BarcodeServlet", urlPatterns = { "/barcodeservlet" })
public class BarcodeServlet extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        int chid = Integer.parseInt( request.getParameter("chid"));
        System.out.println("challan id: "+chid);
        Map parameters = new HashMap();
        parameters.put("p_challan_id", chid);

        InputStream is = null;
        OutputStream os = null;
        Connection conn = null;
        try {
            is = getServletContext().getResourceAsStream("/reports/challan_barcodes.jasper");
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename=barcodes.pdf");
            os = response.getOutputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(is);

            InitialContext initialContext = new InitialContext();
            DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/connDS");

            JasperPrint jasperPrint = null;
            
            if (jasperReport != null && parameters != null && ds.getConnection() != null) {
                conn = ds.getConnection();
                System.out.println("#####################################################################");
                System.out.println("#####################################################################");
                System.out.println("#####################################################################");
                System.out.println("connection is null");
                System.out.println("#####################################################################");
                System.out.println("#####################################################################");
                System.out.println("#####################################################################");
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
                //JasperViewer.viewReport(jasperPrint, false);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
                os.write(baos.toByteArray());
                
            
                os.flush();
               
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (is != null) {
                    is.close();
                }
                if (conn != null){
                    conn.close();
                }
                if(os != null){
                    os.close();
                }

            } catch (Exception localException1) {
                localException1.printStackTrace();
            }
        }
       
  
    }
}
