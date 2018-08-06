package com.usaparel.vpview.managedbeans;

import com.usaparel.vpview.helper.CommonUtil;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.view.rich.component.rich.output.RichOutputText;

import oracle.binding.BindingContainer;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.ApplicationModule;

public class SessionHandler {
   
    public SessionHandler() {
    }

    public void setUserSession(String username,int userid){
        ADFContext.getCurrent().getSessionScope().put("username", username);
        ADFContext.getCurrent().getSessionScope().put("userid", userid);
        
    }


    public void logoutUser() {
        //CommonUtil.destroySession();
        CommonUtil.setUserSession("username", null);
        CommonUtil.setUserSession("userid", null);
        
                
              
        
        
    }
    
    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public void showError() {
        // Add event code here...
        FacesMessage Message = new FacesMessage("Invalid Username or Password.");   
        Message.setSeverity(FacesMessage.SEVERITY_ERROR);   
        FacesContext fc = FacesContext.getCurrentInstance();   
        fc.addMessage(null, Message);  
    }
}
