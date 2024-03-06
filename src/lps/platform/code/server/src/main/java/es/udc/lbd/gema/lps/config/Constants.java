/*% if (checkAnyEntityContainsPatternOfType(data, 'ipPattern') ||
        checkAnyEntityContainsPatternOfType(data, 'urlPattern') ||
        checkAnyEntityContainsPatternOfType(data, 'emailPattern') ||
        feature.UserManagement || feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.config;

public class Constants {
    /*% if (checkAnyEntityContainsPatternOfType(data, 'ipPattern')) { %*/

    // Regular expression used to validate IPs
    public static final String IP_REGEX = "^(([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).){3}([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    /*% } %*/
    /*% if (checkAnyEntityContainsPatternOfType(data, 'urlPattern')) { %*/

    // Regular expression used to validate URLs
    public static final String URL_REGEX = "^((https?|ftps?)://)?(www.)?[-a-zA-Z0-9@:%._+~#=]{2,256}.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&//=]*)$";
    /*% } %*/
    /*% if (checkAnyEntityContainsPatternOfType(data, 'emailPattern')) { %*/

    // Regular expression used to validate emails
    public static final String EMAIL_REGEX = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@[a-z0-9-]+(.[a-z0-9-]+)*(.[a-z]{2,3})$";
    /*% } %*/
    /*% if (feature.UserManagement) { %*/

    public static final String SYSTEM_ACCOUNT = "system";

    // Regexp for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";
    /*% } %*/
    /*% if (feature.DM_A_G_Batch) { %*/

    // Google Maps Key
    // Generation instructions: https://developers.google.com/maps/documentation/javascript/get-api-key
    public static final String GOOGLE_MAPS_KEY = "AIzaSyCChpZobaT7XoOR2fjQM24H6-iEYogPO5I";
    /*% } %*/
}
/*% } %*/