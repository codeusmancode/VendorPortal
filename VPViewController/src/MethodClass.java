import com.usaparel.vpview.UserBean;

import javax.faces.context.FacesContext;

import javax.servlet.http.HttpSession;

import oracle.adf.share.ADFContext;

public class MethodClass {
    public MethodClass() {
    }

    public void method() {
        System.out.println("Inside a method");
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.setAttribute("username", "pakistan");
        
    }

    public String lkogin(String username) {
        System.out.println("name is "+username);
        return "return statement "+username;
    }
}
