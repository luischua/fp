package business;

import java.math.BigDecimal;

public class Discount {

    public static BigDecimal calculateDiscount(String name){
        BigDecimal value = new BigDecimal(1);
        if(!name.equals("NET")){
            String[] splitString = name.split("-");
            for(String s : splitString) {
                int percent = Integer.parseInt(s.replace("%", ""));
                BigDecimal percentInValue = new BigDecimal(100)
                        .subtract(new BigDecimal(percent))
                        .divide(new BigDecimal(100));
                value = value.multiply(percentInValue);
            }
        }
        return value;
    }
}
