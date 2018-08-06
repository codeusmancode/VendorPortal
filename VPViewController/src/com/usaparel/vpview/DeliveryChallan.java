package com.usaparel.vpview;

import com.tangosol.coherence.component.net.extend.Message;

import com.usaparel.vpview.helper.ADFUtil;

import java.sql.Date;

import javax.el.ELContext;

import javax.el.ExpressionFactory;

import javax.el.MethodExpression;

import javax.el.ValueExpression;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputText;

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.binding.BindingContainer;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;

import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;


public class DeliveryChallan {
    private int quantity;
    private RichInputText itPackingSize;
    private RichInputText itDivisor;
    private RichInputText itChallanID;
    private RichInputText itChallanStatus;
    private RichInputText itQty;

    public DeliveryChallan() {
    }

    private ViewObject getViewObject(String iterator) {
        BindingContainer bindings = getBindings();
        DCBindingContainer bc = (DCBindingContainer) bindings;
        DCIteratorBinding it = bc.findIteratorBinding(iterator);
        return it.getViewObject();
    }

    private void showMessage(String message) {
        FacesMessage Message = new FacesMessage(message);
        Message.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, Message);
    }

    public String generate() {
        // Add event code here...
        Row challanRow = (Row) ADFUtil.evaluateEL("#{bindings.VOCustShipmentDeliveryChallan1Iterator.currentRow}");
        if (challanRow == null) {
            showMessage("Create Challan First");
            return null;
        }


        int challanID = Integer.parseInt(challanRow.getAttribute("ChallanId").toString());
        int packingSize =
            (itPackingSize.getValue() == null) ? 0 : Integer.parseInt(getItPackingSize().getValue().toString());
        int packingDivisor =
            getItDivisor().getValue() == null ? 0 : Integer.parseInt(getItDivisor().getValue().toString());
        if (packingSize != 0 && packingDivisor != 0) {
            showMessage("Provide just one value, Packing Size OR Packing Divisor.");
            return null;
        }

        //clear();

        Row poRow = (Row) ADFUtil.evaluateEL("#{bindings.VOXXCustPoHeader1Iterator.currentRow}");
        Row poLineRow = (Row) ADFUtil.evaluateEL("#{bindings.VOXXCustPoLines3Iterator.currentRow}");
        ViewObject vo = getViewObject("VOXXCustShipmentPacking3Iterator");
        if (packingDivisor >
            0) {
            //CREATE PACKING LINES USING PACKING DEVISOR FACTOR
            createPacking("DIVISOR", packingDivisor, getQuantity(), challanID,
                          Integer.parseInt(poRow.getAttribute("PoHeaderId").toString()),
                          Integer.parseInt(poLineRow.getAttribute("PoLineId").toString()),
                          Integer.parseInt(poLineRow.getAttribute("ItemId").toString()), vo);
            vo.executeQuery();
            showMessage("Packs generated.");

        } else if (packingSize > 0) {
            createPacking("SIZE", packingSize, getQuantity(), challanID,
                          Integer.parseInt(poRow.getAttribute("PoHeaderId").toString()),
                          Integer.parseInt(poLineRow.getAttribute("PoLineId").toString()),
                          Integer.parseInt(poLineRow.getAttribute("ItemId").toString()), vo);
            showMessage("Packs generated.");
        }else{
            showMessage("Wrong Size or Packs Given.");
        }

        //reset
        getItDivisor().resetValue();
        getItPackingSize().resetValue();
        AdfFacesContext.getCurrentInstance().addPartialTarget(getItDivisor());
        AdfFacesContext.getCurrentInstance().addPartialTarget(getItPackingSize());
        return null;
    }

    private void createPacking(String type, int value, int shipQuantity, int challanID, int poHeaderId, int poLineId,
                               int itemId, ViewObject voPacking) {
        int packingQuantity = 0;


        if (type.equals("DIVISOR")) {
            int packingLineNum = 1;
            for (int i = 1; i <= value; i++) {
                //IF IT'S LAST PACKING LINE THEN WE NEED TO ADD THE REMINDER IN THE QUANTITY TOO
                if (i == value)
                    packingQuantity = (shipQuantity / value) + (shipQuantity % value);
                else
                    packingQuantity = (shipQuantity / value);
                //CREATE ROW USING vo.CreateRow() method
                Row row = voPacking.createRow();
                row.setAttribute("PoHeaderId", poHeaderId);
                row.setAttribute("PoLineId", poLineId);
                row.setAttribute("ItemId", itemId);
                row.setAttribute("DeliveryChallanId", challanID);
                row.setAttribute("PackingLineNum", packingLineNum);
                packingLineNum = packingLineNum + 1;
                row.setAttribute("PackingQty", packingQuantity);
                row.setAttribute("PackingDate", new Date(System.currentTimeMillis()));
                row.setAttribute("Status", "NEW");
                row.setAttribute("CreatedBy", (ADFContext.getCurrent()
                                                         .getSessionScope()
                                                         .get("userid")
                                                         .toString()));
                row.setAttribute("LastUpdatedBy", (ADFContext.getCurrent()
                                                             .getSessionScope()
                                                             .get("userid")
                                                             .toString()));
                row.setAttribute("OriginalChallanQty", packingQuantity);
                System.out.println("Row created");
            }
        } else if (type.equals("SIZE")) {
            int packingSize = value;
            int packingLineNum = 1;

            int totalRows = (int) Math.ceil((double) shipQuantity / packingSize);
            System.out.println("total rows: " + totalRows + " " + shipQuantity + " " + packingSize);
            int packingQty = 0;
            for (int i = 1; i <= totalRows; i++) {
                if (i == totalRows && (shipQuantity % packingSize) > 0) {
                    packingQty = (shipQuantity % packingSize);
                } else {
                    packingQty = packingSize;
                }

                //CREATE ROW USING vo.CreateRow() method
                Row row = voPacking.createRow();
                row.setAttribute("PoHeaderId", poHeaderId);
                row.setAttribute("PoLineId", poLineId);
                row.setAttribute("ItemId", itemId);
                row.setAttribute("DeliveryChallanId", challanID);
                row.setAttribute("PackingLineNum", packingLineNum);
                packingLineNum = packingLineNum + 1;
                row.setAttribute("PackingQty", packingQty);
                row.setAttribute("OriginalChallanQty", packingQty);

                row.setAttribute("PackingDate", new Date(System.currentTimeMillis()));
                row.setAttribute("Status", "NEW");
                row.setAttribute("CreatedBy", (ADFContext.getCurrent()
                                                         .getSessionScope()
                                                         .get("userid")
                                                         .toString()));
                row.setAttribute("LastUpdatedBy", (ADFContext.getCurrent()
                                                             .getSessionScope()
                                                             .get("userid")
                                                             .toString()));
            }
        }

    }

    private BindingContainer getBindings() {

        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    private void clear() {
        ViewObject voPacking = getViewObject("VOXXCustShipmentPacking3Iterator");
        //remove all the rows selected against shipId
        RowSetIterator rsi = voPacking.createRowSetIterator(null);
        while (rsi.hasNext()) {
            rsi.next().remove();
        }
        rsi.closeRowSetIterator();
        voPacking.executeQuery();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void customTblSelectionLsnr(SelectionEvent selectionEvent) {
        String el = "#{bindings.VOXXCustPoLines3.collectionModel.makeCurrent}";
        ADFUtil.invokeEL(el, new Class[] { DeliveryChallan.class }, new Object[] { selectionEvent });
        el = "#{bindings.VOXXCustPoLines3Iterator.currentRow}";
        Row selectedRow = (Row) ADFUtil.evaluateEL(el);
        int lineQuantity = Integer.parseInt(selectedRow.getAttribute("Quantity").toString());
        int packingQuantity =
            (selectedRow.getAttribute("packingSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("packingSum").toString());
        int shortQuantity =
            (selectedRow.getAttribute("packingSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("ShortSum").toString());
        int badQuantity =
            (selectedRow.getAttribute("packingSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("BadSum").toString());
        int missingQuantity =
            (selectedRow.getAttribute("packingSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("MissingSum").toString());
        int totalAcceptedQuantity =
            (selectedRow.getAttribute("TotalAccepted") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("TotalAccepted").toString());
        
        
        el = "#{bindings.VOCustShipmentDeliveryChallan1Iterator.currentRow}";
        Row challanRow = (Row) ADFUtil.evaluateEL(el);

        el = "#{bindings.VOXXCustShipmentPacking3Iterator.estimatedRowCount}";
        int packsCount = Integer.parseInt( ADFUtil.evaluateEL(el).toString());
        
                
//        System.out.println("packing quantity: " + packingQuantity);
        el = "#{DeliveryChallan.quantity}";
        //if (packingQuantity == 0) {
        //ADFUtil.setEL(el, lineQuantity - packingQuantity + (shortQuantity + badQuantity + missingQuantity));
        System.out.println("Setting in po line table listener: "+(lineQuantity - totalAcceptedQuantity));
        
        ADFUtil.setEL(el, lineQuantity - totalAcceptedQuantity);
        //} else {
          //  if (packsCount <= 0) {
            //    ADFUtil.setEL(el, lineQuantity - packingQuantity + (shortQuantity + badQuantity + missingQuantity));
            //} else {
              //  ADFUtil.setEL(el, packingQuantity);
            //}

        //}

    }

    public void poHeaderRowSelection(SelectionEvent selectionEvent) {
        String el = "#{bindings.VOXXCustPoHeader1.collectionModel.makeCurrent}";
        ADFUtil.invokeEL(el, new Class[] { DeliveryChallan.class }, new Object[] { selectionEvent });
        el = "#{bindings.VOXXCustPoLines3Iterator.currentRow}";
        Row selectedRow = (Row) ADFUtil.evaluateEL(el);
        int lineQuantity = Integer.parseInt(selectedRow.getAttribute("Quantity").toString());
        int packingQuantity =
            (selectedRow.getAttribute("packingSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("packingSum").toString());
        int shortQuantity =
            (selectedRow.getAttribute("ShortSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("ShortSum").toString());
        int badQuantity =
            (selectedRow.getAttribute("BadSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("BadSum").toString());
        int missingQuantity =
            (selectedRow.getAttribute("MissingSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("MissingSum").toString());
        int totalAcceptedQuantity =
            (selectedRow.getAttribute("TotalAccepted") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("TotalAccepted").toString());


        el = "#{bindings.VOXXCustShipmentPacking3Iterator.estimatedRowCount}";
        int packsCount = Integer.parseInt( ADFUtil.evaluateEL(el).toString());
        
        el = "#{DeliveryChallan.quantity}";
        System.out.println("Setting in po header table listener:"+(lineQuantity -totalAcceptedQuantity));
 //       if (packingQuantity == 0) {
            ADFUtil.setEL(el, lineQuantity -totalAcceptedQuantity);
   //     } else {
       //     if (packsCount <= 0) {
     //           ADFUtil.setEL(el, lineQuantity - packingQuantity + (shortQuantity + badQuantity + missingQuantity));
         ////   } else {
             //   ADFUtil.setEL(el, packingQuantity);
            //}

        //}

        //ADFUtil.setEL(el, lineQuantity - packingQuantity + (badQuantity + shortQuantity + missingQuantity));

        //setQuantity(lineQuantity);


    }

    public void setItPackingSize(RichInputText itPackingSize) {
        this.itPackingSize = itPackingSize;
    }

    public RichInputText getItPackingSize() {
        return itPackingSize;
    }

    public void setItDivisor(RichInputText itDivisor) {
        this.itDivisor = itDivisor;
    }

    public RichInputText getItDivisor() {
        return itDivisor;
    }

    public void quantityValidator(FacesContext facesContext, UIComponent uIComponent, Object object) {
        // Add event code here...
        // System.out.println(object.toString());



    }

    public String shipChallan() {

        return null;
    }

    public void quantityChanged(ValueChangeEvent valueChangeEvent) {
        String el = "#{bindings.VOXXCustPoLines3Iterator.currentRow}";
        Row selectedRow = (Row) ADFUtil.evaluateEL(el);
        int lineQuantity = Integer.parseInt(selectedRow.getAttribute("Quantity").toString());
        int packingQuantity =
            (selectedRow.getAttribute("packingSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("packingSum").toString());
        int shortQuantity =
            (selectedRow.getAttribute("ShortSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("ShortSum").toString());
        int badQuantity =
            (selectedRow.getAttribute("BadSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("BadSum").toString());
        int missingQuantity =
            (selectedRow.getAttribute("MissingSum") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("MissingSum").toString());
        
        int totalAcceptedQuantity =
            (selectedRow.getAttribute("TotalAccepted") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("TotalAccepted").toString());
        
        
        System.out.println("old value: " + valueChangeEvent.getOldValue() + " new value: " +
                           valueChangeEvent.getNewValue());
        if (Integer.parseInt(valueChangeEvent.getNewValue().toString()) <= 0 ||
            (Integer.parseInt(valueChangeEvent.getNewValue().toString()) >
             (lineQuantity - totalAcceptedQuantity))) {
            getItQty().setValue(lineQuantity - totalAcceptedQuantity);
            AdfFacesContext.getCurrentInstance().addPartialTarget(getItQty());
        }
    }

    public String printBarcodes() {
//        if (!getItChallanStatus().getValue()
//                                 .toString()
//                                 .equals("SHIPPED")) {
//            showMessage("Ship this challan to print barcodes.");
//            return "";
//        }
        try {
            String chid = getItChallanID().getValue().toString();
            //FacesContext.getCurrentInstance().getExternalContext().redirect("/VendorPortal-VPViewController-context-root/barcodeservlet?pono=44976");
            //FacesContext.getCurrentInstance().responseComplete();

            ExtendedRenderKitService erks =
                Service.getRenderKitService(FacesContext.getCurrentInstance(), ExtendedRenderKitService.class);
            String strWinOpen =
                "window.open('" + "/VendorPortal-VPViewController-context-root" + "/barcodeservlet?chid=" + chid + "'" +
                ",'','width=800,height=600,directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no');";
            erks.addScript(FacesContext.getCurrentInstance(), strWinOpen);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setItChallanID(RichInputText itChallanID) {
        this.itChallanID = itChallanID;
    }

    public RichInputText getItChallanID() {
        return itChallanID;
    }

    public void setItChallanStatus(RichInputText itChallanStatus) {
        this.itChallanStatus = itChallanStatus;
    }

    public RichInputText getItChallanStatus() {
        return itChallanStatus;
    }

    public void setItQty(RichInputText itQty) {
        this.itQty = itQty;
    }

    public RichInputText getItQty() {
        return itQty;
    }

    public String saveChallan() {

        int packQuantity = 0;
        ViewObject voPacking = getViewObject("VOXXCustShipmentPacking3Iterator");
        //remove all the rows selected against shipId
        RowSetIterator rsi = voPacking.createRowSetIterator(null);
        while (rsi.hasNext()) {
            Row r = rsi.next();
            packQuantity = packQuantity + Integer.parseInt(r.getAttribute("PackingQty").toString());
        }
        rsi.closeRowSetIterator();
        voPacking.executeQuery();
        
        String el = "#{bindings.VOXXCustPoLines3Iterator.currentRow}";
        Row selectedRow = (Row) ADFUtil.evaluateEL(el);
        
        int lineQuantity = Integer.parseInt(selectedRow.getAttribute("Quantity").toString());
        
        int totalAcceptedQuantity =
            (selectedRow.getAttribute("TotalAccepted") == null) ? 0 :
            Integer.parseInt(selectedRow.getAttribute("TotalAccepted").toString());
        
        
        System.out.println(packQuantity+" <-> "+getItQty().getValue().toString());
        if (packQuantity > (lineQuantity-totalAcceptedQuantity)) {
            showMessage("Total Quantity should be equal to the sum of individual pack quantity");
            return null;
        }


        BindingContainer bindings = getBindings();
        OperationBinding operationBinding = bindings.getOperationBinding("Commit");
        Object result = operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            return null;
        }
        return null;
    }
}
