package com.usaparel.vpview.helper;

import javax.faces.context.FacesContext;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.TaskFlowId;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.fragment.RichRegion;

public class TeamplateActions {
    
    public TeamplateActions() {
    }

    public String logoutAction() {
        
        CommonUtil.destroySession();
                                                                   
                                                                                         

        return null;
    }

}
