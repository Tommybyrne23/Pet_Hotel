package jakarta.faces.context;

/* 
 * information on this has been taken from the link below
 *  
 * https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html
 * 
 * We're extending the faces Context elements for the purposes of JUNIT Testing 
 * to cover PayPal Payments
 */

public abstract class FacesContextMocker extends FacesContext {
    public static void setContext(FacesContext context) {
        FacesContext.setCurrentInstance(context);
    }
}