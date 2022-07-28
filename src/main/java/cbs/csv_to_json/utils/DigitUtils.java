package cbs.csv_to_json.utils;

/**
 * Various methods to check and transform digit strings
 */
public class DigitUtils {

    /**
     * Is the string a decimal number? 
     * Works with decimals with a coma (3,1415) & with points (3.1415)
     * @param str   : the string to check
     * @return      : is the string a decimal?
     */
    public static boolean isDecimal(final String str) {
        return str.matches("-?\\d*((\\.|\\,)\\d+)");
    }

     /**
     * Is the string an integer number? 
     * @param str   : the string to check
     * @return      : is the string an integer?
     */
    public static boolean isInt(final String str) {
        return str.matches("-?\\d+");
    }

    /**
     * Transforms a decimal point number to a coma decimal number (3.14 -> 3,14)
     * If the string is not a digit, returns the same string 
     * @param str
     * @return
     */
    public static String pointDecimalToComa(final String str) {
        if (isDecimal(str)) {
            return str.replace(".", ",");
        }
        return str;
        
    }
    
    /**
     * Transforms a coma decimal number to a point decimal number (3,14 -> 3.14)
     * If the string is not a digit, returns the same string 
     * @param str
     * @return
     */
    public static String comaDecimalToPoint(final String str) {
        if (isDecimal(str)) {
            return str.replace(",", ".");
        }

        return str;
        
    }

}
