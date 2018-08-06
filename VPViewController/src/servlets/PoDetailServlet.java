package servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.sql.Connection;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import javax.sql.DataSource;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.CsvReportConfiguration;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.ReportExportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.WriterExporterOutput;
import net.sf.jasperreports.view.JasperViewer;

@WebServlet(name = "PoDetailServlet", urlPatterns = { "/podetailservlet" })
public class PoDetailServlet extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

  
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        int vendor_id = Integer.parseInt(request.getParameter("vendor_id").toString());
        int pono = Integer.parseInt(request.getParameter("pono").toString());
        
        Map parameters = new HashMap();
        parameters.put("vendor_id", vendor_id);
        parameters.put("pono", pono);

        InputStream is = null;
        OutputStream os = null;
        Connection conn = null;
        try {
            is = getServletContext().getResourceAsStream("/reports/purchaseorder_details.jasper");
            response.setContentType("text/csv");
            response.addHeader("Content-Disposition", "attachment; filename=po.csv");
            
//            response.setContentType("application/pdf");
//            response.addHeader("Content-Disposition", "attachment; filename=barcodes.pdf");
            
            os = response.getOutputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(is);

            InitialContext initialContext = new InitialContext();
            DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/connDS");

            JasperPrint jasperPrint = null;
            
            if (jasperReport != null && parameters != null && ds.getConnection() != null) {
                conn = ds.getConnection();
               
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
                JRCsvExporter exporter = new JRCsvExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleWriterExporterOutput(os));
                
              //old method
                
//                exporterCSV.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
//                exporterCSV.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, os);
//                exporterCSV.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, true);
//                exporterCSV.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, true);
//                exporterCSV.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, true);
//                
                exporter.exportReport();
               
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
