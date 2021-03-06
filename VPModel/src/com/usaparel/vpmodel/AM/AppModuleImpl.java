package com.usaparel.vpmodel.AM;

import com.usaparel.vpmodel.AM.common.AppModule;

import com.usaparel.vpmodel.VO.VOChallanSummaryPOHeadersImpl;

import com.usaparel.vpmodel.VO.VOChallanSummaryPOLinesImpl;


import com.usaparel.vpmodel.VO.VOCustShipmentDeliveryChallanRowImpl;
import com.usaparel.vpmodel.VO.VOInvalidChallanQtyImpl;

import java.sql.CallableStatement;

import java.sql.Types;

import java.util.Date;

import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.ViewLinkImpl;
import oracle.jbo.server.ViewObjectImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Fri Jul 21 13:47:27 PKT 2017
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class AppModuleImpl extends ApplicationModuleImpl implements AppModule {
    /**
     * This is the default constructor (do not remove).
     */
    public AppModuleImpl() {
    }

    /**
     * Container's getter for VOXXCustPoHeader1.
     * @return VOXXCustPoHeader1
     */
    public ViewObjectImpl getVOXXCustPoHeader1() {
        return (ViewObjectImpl) findViewObject("VOXXCustPoHeader1");
    }
    
    /**
     *
     * @param pono
     * @param shipQty
     * @param totalPacks
     */
    public void sendNotification(String vendor,int pono,int shipQty,int totalPacks){
        int returnStatus;
        
        
        String body="<html>\n" + 
        "    <head>\n" + 
        "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" + 
        "        <title>email_template</title>\n" + 
        "    </head>\n" + 
        "    <body>\n" + 
        "        <h3>Following order has been shipped on <i>" +new Date(System.currentTimeMillis()).toString()+"</i> </h3>     "+
        "        <table style=\"background-color: #f7f7f7;\">\n" + 
        "            <tr>\n" + 
        "            \n" + 
        "                <td>Vendor Name:</td>\n" + 
        "                <td>"+vendor+"</td>\n" + 
        "            </tr>\n" + 
        "            <tr>\n" + 
        "            \n" + 
        "                <td>Po Number:</td>\n" + 
        "                <td>"+pono+"</td>\n" + 
        "            </tr>\n" + 
        "            <tr>\n" + 
        "            \n" + 
        "                <td>Ship Quantity:</td>\n" + 
        "                <td>"+shipQty+"</td>\n" + 
        "            </tr>\n" + 
        "            <tr>\n" + 
        "            \n" + 
        "                <td>Total Packs</td>\n" + 
        "                <td>"+totalPacks+"</td>\n" + 
        "            </tr>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>";
        CallableStatement cst = null;
        try {
            cst =
                this.getDBTransaction()
                .createCallableStatement("begin " + "SENDMAIL1.SMAIL(?,?,?,?,?)" + "; end;", 0);
            
            cst.setObject(1,"usmanriaz@usaparel.com");
            cst.setObject(2, "usmanriaz@usaparel.com");
            cst.setObject(3, "Test Email");
            cst.setObject(4, body);
            cst.setObject(5, null);
            cst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
         
        } finally {
            if (cst != null) {
                try {
                    cst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     *
     * @param username
     * @param password
     * @return
     */
    public int login(String username, String password) {
        int returnStatus;
        CallableStatement cst = null;
        try {
            cst =
                this.getDBTransaction()
                .createCallableStatement("begin ? := " + "xx_cust_auth.login(?,?)" + "; end;", 0);
            cst.registerOutParameter(1, Types.VARCHAR);
            cst.setObject(2, username);
            cst.setObject(3, password);
            cst.executeUpdate();

            int returnFromMethod = cst.getInt(1);
            
            getVOCustShipmentDeliveryChallan1().executeQuery();
            getVOXXCustPoHeader1().executeQuery();
            getVOXXCustPoLines3().executeQuery();
            getVOXXCustShipmentPacking3().executeQuery();
            getVOChallanSummaryPOHeaders1().executeQuery();
            
            
            return  returnFromMethod;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (cst != null) {
                try {
                    cst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    

    /**
     * Container's getter for VOXXCustPoLines1.
     * @return VOXXCustPoLines1
     */
    public ViewObjectImpl getVOXXCustPoLines1() {
        return (ViewObjectImpl) findViewObject("VOXXCustPoLines1");
    }


    /**
     * Container's getter for VOXXCustPoLines3.
     * @return VOXXCustPoLines3
     */
    public ViewObjectImpl getVOXXCustPoLines3() {
        return (ViewObjectImpl) findViewObject("VOXXCustPoLines3");
    }

    /**
     * Container's getter for VlPoHeaderLines1.
     * @return VlPoHeaderLines1
     */
    public ViewLinkImpl getVlPoHeaderLines1() {
        return (ViewLinkImpl) findViewLink("VlPoHeaderLines1");
    }

    /**
     * Container's getter for VOXXCustShipmentLines1.
     * @return VOXXCustShipmentLines1
     */
    public ViewObjectImpl getVOXXCustShipmentLines1() {
        return (ViewObjectImpl) findViewObject("VOXXCustShipmentLines1");
    }

    /**
     * Container's getter for VOXXCustShipmentLines2.
     * @return VOXXCustShipmentLines2
     */
    public ViewObjectImpl getVOXXCustShipmentLines2() {
        return (ViewObjectImpl) findViewObject("VOXXCustShipmentLines2");
    }

    /**
     * Container's getter for VlPoLinesShipmentLines1.
     * @return VlPoLinesShipmentLines1
     */
    public ViewLinkImpl getVlPoLinesShipmentLines1() {
        return (ViewLinkImpl) findViewLink("VlPoLinesShipmentLines1");
    }

    /**
     * Container's getter for VOXXCustShipmentPacking1.
     * @return VOXXCustShipmentPacking1
     */
    public ViewObjectImpl getVOXXCustShipmentPacking1() {
        return (ViewObjectImpl) findViewObject("VOXXCustShipmentPacking1");
    }

    /**
     * Container's getter for VOXXCustShipmentPacking2.
     * @return VOXXCustShipmentPacking2
     */
    public ViewObjectImpl getVOXXCustShipmentPacking2() {
        return (ViewObjectImpl) findViewObject("VOXXCustShipmentPacking2");
    }

    /**
     * Container's getter for VlShipLinesShipPacking1.
     * @return VlShipLinesShipPacking1
     */
    public ViewLinkImpl getVlShipLinesShipPacking1() {
        return (ViewLinkImpl) findViewLink("VlShipLinesShipPacking1");
    }


    /**
     * Container's getter for LOVStatus1.
     * @return LOVStatus1
     */
    public ViewObjectImpl getLOVStatus1() {
        return (ViewObjectImpl) findViewObject("LOVStatus1");
    }

    /**
     * Container's getter for VOCustShipmentDeliveryChallan1.
     * @return VOCustShipmentDeliveryChallan1
     */
    public ViewObjectImpl getVOCustShipmentDeliveryChallan1() {
        return (ViewObjectImpl) findViewObject("VOCustShipmentDeliveryChallan1");
    }

    /**
     * Container's getter for VOXXCustShipmentPacking3.
     * @return VOXXCustShipmentPacking3
     */
    public ViewObjectImpl getVOXXCustShipmentPacking3() {
        return (ViewObjectImpl) findViewObject("VOXXCustShipmentPacking3");
    }

    /**
     * Container's getter for VLChallanPacking1.
     * @return VLChallanPacking1
     */
    public ViewLinkImpl getVLChallanPacking1() {
        return (ViewLinkImpl) findViewLink("VLChallanPacking1");
    }

    /**
     * Container's getter for LovMedium1.
     * @return LovMedium1
     */
    public ViewObjectImpl getLovMedium1() {
        return (ViewObjectImpl) findViewObject("LovMedium1");
    }

    /**
     * Container's getter for LovVehicleType1.
     * @return LovVehicleType1
     */
    public ViewObjectImpl getLovVehicleType1() {
        return (ViewObjectImpl) findViewObject("LovVehicleType1");
    }

    /**
     * Container's getter for VOXXCustShipmentPacking4.
     * @return VOXXCustShipmentPacking4
     */
    public ViewObjectImpl getVOXXCustShipmentPacking4() {
        return (ViewObjectImpl) findViewObject("VOXXCustShipmentPacking4");
    }

    /**
     * Container's getter for VLPOLinesPacking1.
     * @return VLPOLinesPacking1
     */
    public ViewLinkImpl getVLPOLinesPacking1() {
        return (ViewLinkImpl) findViewLink("VLPOLinesPacking1");
    }

    /**
     * Container's getter for VOChallanSummary1.
     * @return VOChallanSummary1
     */
    public ViewObjectImpl getVOChallanSummary1() {
        return (ViewObjectImpl) findViewObject("VOChallanSummary1");
    }

    /**
     * Container's getter for VOChallanSummaryPOHeaders1.
     * @return VOChallanSummaryPOHeaders1
     */
    public VOChallanSummaryPOHeadersImpl getVOChallanSummaryPOHeaders1() {
        return (VOChallanSummaryPOHeadersImpl) findViewObject("VOChallanSummaryPOHeaders1");
    }

    /**
     * Container's getter for VOChallanSummaryPOLines1.
     * @return VOChallanSummaryPOLines1
     */
    public VOChallanSummaryPOLinesImpl getVOChallanSummaryPOLines1() {
        return (VOChallanSummaryPOLinesImpl) findViewObject("VOChallanSummaryPOLines1");
    }

    /**
     * Container's getter for VOChallanSummaryPOLines2.
     * @return VOChallanSummaryPOLines2
     */
    public VOChallanSummaryPOLinesImpl getVOChallanSummaryPOLines2() {
        return (VOChallanSummaryPOLinesImpl) findViewObject("VOChallanSummaryPOLines2");
    }

    /**
     * Container's getter for VLChallanSummary1.
     * @return VLChallanSummary1
     */
    public ViewLinkImpl getVLChallanSummary1() {
        return (ViewLinkImpl) findViewLink("VLChallanSummary1");
    }

    /**
     * Container's getter for LOVChalans1.
     * @return LOVChalans1
     */
    public ViewObjectImpl getLOVChalans1() {
        return (ViewObjectImpl) findViewObject("LOVChalans1");
    }

    /**
     * Container's getter for VOInvalidChallanQty1.
     * @return VOInvalidChallanQty1
     */
    public VOInvalidChallanQtyImpl getVOInvalidChallanQty1() {
        return (VOInvalidChallanQtyImpl) findViewObject("VOInvalidChallanQty1");
    }


    /**
     * Container's getter for VOInvalidChallanQty2.
     * @return VOInvalidChallanQty2
     */
    public VOInvalidChallanQtyImpl getVOInvalidChallanQty2() {
        return (VOInvalidChallanQtyImpl) findViewObject("VOInvalidChallanQty2");
    }

    /**
     * Container's getter for VLSummaryDetailSummary1.
     * @return VLSummaryDetailSummary1
     */
    public ViewLinkImpl getVLSummaryDetailSummary1() {
        return (ViewLinkImpl) findViewLink("VLSummaryDetailSummary1");
    }

    /**
     * Container's getter for LOVMissingBox1.
     * @return LOVMissingBox1
     */
    public ViewObjectImpl getLOVMissingBox1() {
        return (ViewObjectImpl) findViewObject("LOVMissingBox1");
    }
}

