package com.usaparel.vpview.managedbeans;



import com.usaparel.vpmodel.AM.common.AppModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.sql.Date;

import java.util.Map;

import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;

import net.sf.jasperreports.engine.xml.JRXmlLoader;

import oracle.adf.controller.TaskFlowId;
import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCBindingContainer;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.output.RichOutputText;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;

/**
 * This class is used as a backing bean for vendorPO.jsp. Every method has error checking conditions first and then the rest of the proceedings.
 */
public class VendorPo {
    private String taskFlowId = "/WEB-INF/tf-vendor-portal.xml#tf-vendor-portal";
    private RichInputText itPoNo;
    private RichInputText itVendorID;
    private RichInputText itVendorName;

    public VendorPo() {
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public String createShipmentPlanLine() {
        
        BindingContainer bindings = getBindings();
        DCBindingContainer bc = (DCBindingContainer)bindings;
        
        System.out.println(" createShipmentPlanLine MEthod.");
        
        /*****
         *  GET CURRENT PO LINE
         *  THIS CODE SHOULD EXECUTE BEFORE CREATE_INSERT METHOD BINDING INVOKATION
         *  BECAUSE NEW ROW IN SHIPMENT LINES  MIGHT EFFECT MAX SHIP_NO
         *****/
         
        DCIteratorBinding iterator = bc.findIteratorBinding("VOXXCustPoLines3Iterator");
        Row poLineCurRow = iterator.getCurrentRow();
        int poLineId = Integer.parseInt(poLineCurRow.getAttribute("PoLineId").toString());
        int maxShipNo = Integer.parseInt(poLineCurRow.getAttribute("MaxShipNumber")==null?"0":poLineCurRow.getAttribute("MaxShipNumber").toString());
        int totalShipmentQty = Integer.parseInt((poLineCurRow.getAttribute("TotalShipmentQty")==null?"0":poLineCurRow.getAttribute("TotalShipmentQty").toString()));
        int poLineQty = Integer.parseInt(poLineCurRow.getAttribute("Quantity").toString());
        String shipStatus = "PACKING";
        
        System.out.println("po_line_id: "+poLineId);
        System.out.println("max_ship_no: "+maxShipNo);
        System.out.println("total_shipment_qty: "+totalShipmentQty);
        System.out.println("Po_line_qty: "+poLineQty);
        
        //SHOW ERROR IF TOTAL SHIPMENT QUANTITY IS = PO LINE QUANTITY
        if (poLineQty - totalShipmentQty <=0 ){
            FacesMessage Message = new FacesMessage("Quantity Exceeds.");   
            Message.setSeverity(FacesMessage.SEVERITY_ERROR);   
            FacesContext fc = FacesContext.getCurrentInstance();   
            fc.addMessage(null, Message);    
            return null;
        }  
        
        //CREATE  NEW ROW USING APPLICATION MODULE METHOD BINDING
        OperationBinding operationBinding = bindings.getOperationBinding("CreateInsert");
        Object result = operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            return null;
        }
        
        //GET CURRENT SHIPMENT LINE i.e. newly created shipment line AND fill in defaults
        iterator = bc.findIteratorBinding("VOXXCustShipmentLines2Iterator");
        Row r = iterator.getCurrentRow();
        r.setAttribute("ShipQty", 100);
        r.setAttribute("ShipLineNum", (maxShipNo+1));
        r.setAttribute("PoLineId", poLineId);
        r.setAttribute("ShipQty", (poLineQty-totalShipmentQty));
        r.setAttribute("Status", shipStatus);
        r.setAttribute("ShipDate",new Date(System.currentTimeMillis()));
        r.setAttribute("CreatedBy", (ADFContext.getCurrent().getSessionScope().get("userid").toString()));
        r.setAttribute("LastUpdatedBy", (ADFContext.getCurrent().getSessionScope().get("userid").toString()));
        iterator.executeQuery();
        
        return null;
    }

    public String commitAction() {
        //commit action will be called at the time of inserting and updating.
        //this action is for the time when user is updating a row
        //here we will check if the user is updating a row with quantity that is greater
        //then po line quantity.
        
        ViewObject voPacking = null;
         RowSetIterator  rsi= null;
        
        BindingContainer bindings = getBindings();
        DCBindingContainer bc = (DCBindingContainer)bindings;
        DCIteratorBinding iterator = bc.findIteratorBinding("VOXXCustPoLines3Iterator");
        Row poLineCurRow = iterator.getCurrentRow();
        int totalShipmentQty = Integer.parseInt((poLineCurRow.getAttribute("TotalShipmentQty")==null?"0":poLineCurRow.getAttribute("TotalShipmentQty").toString()));
        int poLineQty = Integer.parseInt(poLineCurRow.getAttribute("Quantity").toString());
        
        
        //error if total quntity is greater then po line quantity.
        if (totalShipmentQty > poLineQty ){
            FacesMessage Message = new FacesMessage("Quantity Exceeds.");   
            Message.setSeverity(FacesMessage.SEVERITY_ERROR);   
            FacesContext fc = FacesContext.getCurrentInstance();   
            fc.addMessage(null, Message);    
            return null;
        }  
        
        /**
         * BELOW WE ARE CHECKING IF USER HAS CHANGED THE PACKING QUANTITY AND THE PACKING QUANTITY IS NOT MATCHING 
         * SHPMENT LINE QUANTITY. IF THAT IS THE CASE USER WILL SEE THE ERROR AND HE HAS TO CORRECT IT. I.E. CHANGE THE
         * QUANTITY OF SHIPMENT LINE.
         */
        iterator = bc.findIteratorBinding("VOXXCustShipmentLines2Iterator");
        Row shipCurRow = iterator.getCurrentRow();
        
        int packingSize = Integer.parseInt((shipCurRow.getAttribute("PackingSize")==null?"0":shipCurRow.getAttribute("PackingSize").toString()));
        int packingDivisor = Integer.parseInt((shipCurRow.getAttribute("PackingDivisor")==null?"0":shipCurRow.getAttribute("PackingDivisor").toString()));
        int shipQty = Integer.parseInt((shipCurRow.getAttribute("ShipQty")==null?"0":shipCurRow.getAttribute("ShipQty").toString()));
        int totalPackingQty = Integer.parseInt((shipCurRow.getAttribute("totalPackingQty")==null?"0":shipCurRow.getAttribute("totalPackingQty").toString()));
        
        
        /**
         * get the packing lines with quantity >0 associated with the shipment lines and see if packing lines
         * (with quantity>0) are equal to divisor factor in shipment lines. if not equal tell user to fix it first 
         * before saving the work.Also check the packing quantity and shipment quantity, this should be equal too.
         */
       
        //get the view object associated with VOXXCustShipmentPacking2Iterator
        iterator = bc.findIteratorBinding("VOXXCustShipmentPacking2Iterator");
        voPacking =  iterator.getViewObject();
        //remove all the rows selected against shipId 
        rsi =  voPacking.createRowSetIterator(null);
        int totalPackingLinesWithNonZroQty = 0;
        while (rsi.hasNext()){
            if (Integer.parseInt(rsi.next().getAttribute("PackingQty").toString())>0){
                totalPackingLinesWithNonZroQty = totalPackingLinesWithNonZroQty +1;
            }
        }
        rsi.closeRowSetIterator();
        
        System.out.println(packingDivisor+" "+totalPackingLinesWithNonZroQty);
        
        //error if total packing quantity is not equal to shipment line quantity
        if (packingDivisor >0){
            if ((totalPackingQty != shipQty) || (packingDivisor != totalPackingLinesWithNonZroQty)){
                showMessage("Packing Quantity must match shipment line quantity and packing lines must match shipment packing divisor");
                return null;
            } 
        }
        
        
        //USER ISN'T ALLOWED TO DO THAT
        if (packingDivisor >0 && packingSize>0){
            FacesMessage Message = new FacesMessage("You have to provide packing divisor or packing size.");   
            Message.setSeverity(FacesMessage.SEVERITY_ERROR);   
            FacesContext fc = FacesContext.getCurrentInstance();   
            fc.addMessage(null, Message); 
            
            return null;
        }
        
        //change the shippment flag of packinglines
        //i.e. if user changes the shippment flag of shipment line,ship flag of all the packing lines
        //of that shippment line will also be updated.
        //this task was only for the time of update, but i have to do it in commit.
        
        System.out.println(" here .............");
        String shipStatus = shipCurRow.getAttribute("Status").toString();
                
        //get the view object associated with VOXXCustShipmentPacking2Iterator
        iterator = bc.findIteratorBinding("VOXXCustShipmentPacking2Iterator");
        System.out.println(" here sdfsdfsdf");
         voPacking =  iterator.getViewObject();
        //remove all the rows selected against shipId 
         rsi =  voPacking.createRowSetIterator(null);
        while (rsi.hasNext()){
            rsi.next().setAttribute("Status", shipStatus);
        //            System.out.println("deleting row from view object");
        }
        rsi.closeRowSetIterator();
        voPacking.executeQuery();
        
        
        System.out.println(shipStatus+" ship status");
        if (shipStatus.equals("SHIPPED")){
            if (totalPackingQty>0) {
                 //SEND EMAIL NOTIFICATION 
                int pono = Integer.parseInt(getItPoNo().getValue().toString());
                String vendorName = getItVendorName().getValue().toString();
                int totalPacks = totalPackingLinesWithNonZroQty;
                int shipQuantity = shipQty;
            
                sendNotification(vendorName, pono, shipQuantity, totalPacks);
            }else{
                FacesMessage Message = new FacesMessage("You havne't created packings.No Notification Sent.");   
                Message.setSeverity(FacesMessage.SEVERITY_ERROR);   
                FacesContext fc = FacesContext.getCurrentInstance();   
                fc.addMessage(null, Message); 
                //revert the status 
                iterator = bc.findIteratorBinding("VOXXCustShipmentLines2Iterator");
                Row currRow = iterator.getCurrentRow();
                currRow.setAttribute("Status","PACKING");
                iterator.executeQuery();
            }
        }
        
        //commit to database using method bindings 
        OperationBinding operationBinding = bindings.getOperationBinding("Commit");
        Object result = operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            return null;
        }
        
        
        return null;
    }

    public String createPackingPlan() {
        BindingContainer bindings = getBindings();
        DCBindingContainer bc = (DCBindingContainer)bindings;
        DCIteratorBinding iterator = bc.findIteratorBinding("VOXXCustShipmentLines2Iterator");
        Row shipLineCurRow = iterator.getCurrentRow();
        
        int packingDivisor = Integer.parseInt(shipLineCurRow.getAttribute("PackingDivisor")==null ?"0":shipLineCurRow.getAttribute("PackingDivisor").toString());
        int packingSize = Integer.parseInt(shipLineCurRow.getAttribute("PackingSize")==null ?"0":shipLineCurRow.getAttribute("PackingSize").toString());
        int shipQuantity = Integer.parseInt(shipLineCurRow.getAttribute("ShipQty").toString());
        int packingQuantity = 0;
        int shipID = Integer.parseInt(shipLineCurRow.getAttribute("ShipId").toString());
        int packingLineNum = 1;
        String shipmentLineCurrStatus = shipLineCurRow.getAttribute("Status").toString();
        
        if (shipmentLineCurrStatus.equals("SHIPPED")){
            showMessage("This Line has already been shipped.");
            return null;
        }
        
        String status = "PACKING";
        
        if (packingSize !=0 && packingDivisor !=0){
            FacesMessage Message = new FacesMessage("Provide just one value, Packing Size OR Packing Divisor.");   
            Message.setSeverity(FacesMessage.SEVERITY_ERROR);   
            FacesContext fc = FacesContext.getCurrentInstance();   
            fc.addMessage(null, Message);    
            return null;
        }
        
        //get the view object associated with VOXXCustShipmentPacking2Iterator
        //to create rows. rows can also be creted by using binding methods i.e. CreateInsert
        iterator = bc.findIteratorBinding("VOXXCustShipmentPacking2Iterator");
        ViewObject voPacking =  iterator.getViewObject();
        //remove all the rows selected against shipId 
        RowSetIterator  rsi =  voPacking.createRowSetIterator(null);
        while (rsi.hasNext()){
            rsi.next().remove();
//            System.out.println("deleting row from view object");
        }
        rsi.closeRowSetIterator();
        voPacking.executeQuery();
        
        if (packingDivisor  != 0){
            //CREATE PACKING LINES USING PACKING DEVISOR FACTOR
            for (int i=1; i<= packingDivisor; i++){
                //IF IT'S LAST PACKING LINE THEN WE NEED TO ADD THE REMINDER IN THE QUANTITY TOO
                if (i == packingDivisor)
                    packingQuantity = (shipQuantity/packingDivisor) + (shipQuantity%packingDivisor);
                else
                    packingQuantity = (shipQuantity/packingDivisor);
                
                //CREATE ROW USING vo.CreateRow() method
                Row row = voPacking.createRow();
                row.setAttribute("ShipId", shipID);
                row.setAttribute("PackingLineNum", packingLineNum);
                packingLineNum = packingLineNum +1;
                row.setAttribute("PackingQty", packingQuantity);
                row.setAttribute("PackingDate", new Date(System.currentTimeMillis()));
                row.setAttribute("Status", status);
                row.setAttribute("CreatedBy", (ADFContext.getCurrent().getSessionScope().get("userid").toString()));
                row.setAttribute("LastUpdatedBy", (ADFContext.getCurrent().getSessionScope().get("userid").toString()));
            }    
        }else if(packingSize != 0){
            int totalRows = (int)Math.ceil((double)shipQuantity/packingSize);
            
            int packingQty = 0;
            for (int i=1; i<=totalRows; i++){
                if (i == totalRows && (shipQuantity%packingSize)>0){
                    packingQty = (shipQuantity%packingSize);
                }else{
                    packingQty = packingSize;
                }
                
                //CREATE ROW USING vo.CreateRow() method
                Row row = voPacking.createRow();
                row.setAttribute("ShipId", shipID);
                row.setAttribute("PackingLineNum", packingLineNum);
                packingLineNum = packingLineNum +1;
                row.setAttribute("PackingQty", packingQty);
                row.setAttribute("PackingDate", new Date(System.currentTimeMillis()));
                row.setAttribute("Status", status);
                row.setAttribute("CreatedBy", (ADFContext.getCurrent().getSessionScope().get("userid").toString()));
                row.setAttribute("LastUpdatedBy", (ADFContext.getCurrent().getSessionScope().get("userid").toString()));
            }
        }
        
        return null;
    }


    public TaskFlowId getDynamicTaskFlowId() {
        return TaskFlowId.parse(taskFlowId);
    }

    public void setDynamicTaskFlowId(String taskFlowId) {
        this.taskFlowId = taskFlowId;
    }
    
    private void showMessage(String message){
        FacesMessage Message = new FacesMessage(message);   
        Message.setSeverity(FacesMessage.SEVERITY_ERROR);   
        FacesContext fc = FacesContext.getCurrentInstance();   
        fc.addMessage(null, Message);    
        
    }

    public String openBarcodeReport() {
        try{
            String pono = getItPoNo().getValue().toString();
            //FacesContext.getCurrentInstance().getExternalContext().redirect("/VendorPortal-VPViewController-context-root/barcodeservlet?pono=44976"); 
            //FacesContext.getCurrentInstance().responseComplete();
            
            ExtendedRenderKitService erks = Service.getRenderKitService(FacesContext.getCurrentInstance(), ExtendedRenderKitService.class);
            String strWinOpen ="window.open('" + "/VendorPortal-VPViewController-context-root" + "/barcodeservlet?pono="+pono+"'"+",'','width=800,height=600,directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no');";
            erks.addScript(FacesContext.getCurrentInstance(), strWinOpen);
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        
        return null;
    }
    
    public String openPODetailCSVReport() {
        try{
            String pono = getItPoNo().getValue().toString();
            String vendorID = getItVendorID().getValue().toString();
            
            //FacesContext.getCurrentInstance().getExternalContext().redirect("/VendorPortal-VPViewController-context-root/barcodeservlet?pono=44976"); 
            //FacesContext.getCurrentInstance().responseComplete();
            
            ExtendedRenderKitService erks = Service.getRenderKitService(FacesContext.getCurrentInstance(), ExtendedRenderKitService.class);
            String strWinOpen ="window.open('" + "/VendorPortal-VPViewController-context-root" + "/podetailservlet?pono="+pono+"&vendor_id="+vendorID+"'"+",'','width=800,height=600,directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no');";
            erks.addScript(FacesContext.getCurrentInstance(), strWinOpen);
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        
        return null;
    }

    public void setItPoNo(RichInputText itPoNo) {
        this.itPoNo = itPoNo;
    }

    public RichInputText getItPoNo() {
        return itPoNo;
    }

    public void setItVendorID(RichInputText itVendorID) {
        this.itVendorID = itVendorID;
    }

    public RichInputText getItVendorID() {
        return itVendorID;
    }

    public void setItVendorName(RichInputText itVendorName) {
        this.itVendorName = itVendorName;
    }

    public RichInputText getItVendorName() {
        return itVendorName;
    }

    public String sendNotification(String vendorName, int pono, int shipQuantity, int totalPacks) {
        BindingContainer bindings = getBindings();
        OperationBinding operationBinding = bindings.getOperationBinding("sendNotification");
        Map params = operationBinding.getParamsMap();
        
      
        
        
        params.put("vendor",vendorName); //String vendor,int pono,int shipQty,int totalPacks
        params.put("pono",pono);
        params.put("shipQty",shipQuantity);
        params.put("totalPacks",totalPacks);
        
        Object result = operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            return null;
        }
        return null;
    }
}
