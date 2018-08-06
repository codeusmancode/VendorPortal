package com.usaparel.vpview.helper;


import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpSession;


/*Added by Sarmad*/
public class CommonUtil {
    public CommonUtil() {
        super();
    }
    
    public static void setUserSession(String key, String value) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ExternalContext ec = fctx.getExternalContext();
        HttpSession userSession = (HttpSession)ec.getSession(false);
        userSession.setAttribute(key, value);
    }

    public static Object getSessionValue(String key) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ExternalContext ec = fctx.getExternalContext();
        HttpSession userSession = (HttpSession)ec.getSession(true);
        return userSession.getAttribute(key);
    }

    public static void destroySession() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ExternalContext ec = fctx.getExternalContext();
        HttpSession userSession = (HttpSession)ec.getSession(true);
        userSession.invalidate();
    }
    
    public static boolean isSessionValid()
    {
      if (CommonUtil.getSessionValue(Constants.SESSION_USERID)==null)
        return false;
      else return true;
    }
    public static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}
/*Added by Sarmad*/
