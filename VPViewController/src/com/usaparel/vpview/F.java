package com.usaparel.vpview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.sql.Connection;

import java.util.HashMap;
import com.usaparel.vpview.UserBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.io.OutputStream;

import java.sql.Connection;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;

import javax.naming.InitialContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.view.JasperViewer;
import java.util.Map;

import javax.faces.context.FacesContext;

import javax.naming.InitialContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;

public class F {
    String message;
    
    public F() {
    }

    public String b1_action() {
        // Add event code here...
        return null;
    }

    public void method() {
        // Add event code here...
    }

    public ServletContext getContext() {
        return (ServletContext) getFacesContext().getExternalContext().getContext();
    }

    public HttpServletResponse getResponse() {
        return (HttpServletResponse) getFacesContext().getExternalContext().getResponse();
    }

    public static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
    public void setMessage(String newMessage){
        this.message = newMessage;
    }
    public String getMessage(){
        return this.message;
    }

    public String runReportAction()
      {
       
        Map m = new HashMap();
        //m.put("employeeId", empId);// where employeeId is a jasper report parameter
        try
        {
          runReport("new.jasper", m);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public void runReport(String repPath, java.util.Map param) throws Exception
      {
        Connection conn = null;
        try
        {
          HttpServletResponse response = getResponse();
          ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");  
            response.addHeader("Content-Disposition", "attachment; filename=ClassDataReport.pdf");  
          ServletContext context = getContext();
          InputStream fs = context.getResourceAsStream("/reports/" + repPath);
          JasperReport template = (JasperReport) JRLoader.loadObject(fs);
          template.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
            InitialContext initialContext = new InitialContext();
            DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/connDS");
          conn = ds.getConnection();
          JasperPrint print = JasperFillManager.fillReport(template, param, conn);
            JasperExportManager.exportReportToPdfStream(print, response.getOutputStream()); 
//          ByteArrayOutputStream baos = new ByteArrayOutputStream();
//          JasperExportManager.exportReportToPdfStream(print, baos);
//          out.write(baos.toByteArray());
//          out.flush();
//          out.close();
//          FacesContext.getCurrentInstance().responseComplete();
        }
        catch (Exception jex)
        {
          jex.printStackTrace();
        }
        finally
        {   
            try{
                conn.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public String b2_action() {
        try {
            HttpServletResponse response = getResponse();
            ServletOutputStream out = response.getOutputStream();
            response.setHeader("Cache-Control", "max-age=0");
            response.setContentType("application/pdf");
            ServletContext context = getContext();
            InputStream fs = new FileInputStream(new File(context.getRealPath("/reports/new.jasper")));

            JasperReport template = (JasperReport) JRLoader.loadObject(fs);
            template.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

            JasperPrint print;
            Map<String, Object> m = new HashMap<String, Object>();
            InitialContext initialContext = new InitialContext();
            DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/connDS");

            print = JasperFillManager.fillReport(template, m, ds.getConnection());
            //JasperViewer.viewReport(print, false);
    //            JRPdfExporter exporter = new JRPdfExporter();
    //            exporter.setExporterInput(new SimpleExporterInput(print));
    //            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
    //            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
    //
    //            exporter.setConfiguration(configuration);
    //            exporter.exportReport();

            File pdf = File.createTempFile("D:/output.", ".pdf");
            FileOutputStream ss = new FileOutputStream(pdf);
            JasperExportManager.exportReportToPdfStream(print, ss);
            FacesContext.getCurrentInstance().responseComplete();
            ss.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void valueChanged(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        System.out.println("value: "+valueChangeEvent.getNewValue());
    }
}
