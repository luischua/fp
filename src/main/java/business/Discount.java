package business;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Discount {
    private String name = "NET";
    private BigDecimal value = new BigDecimal(1);

    public void setName(String name){
        this.name = name;
        calculateDiscount();
    }

    private void calculateDiscount(){
        if(!name.equals("NET")){
            value = new BigDecimal(1);
            String[] splitString = name.split("-");
            for(String s : splitString) {
                int percent = Integer.parseInt(s.replace("%", ""));
                BigDecimal percentInValue = new BigDecimal(100)
                        .subtract(new BigDecimal(percent))
                        .divide(new BigDecimal(100));
                value = value.multiply(percentInValue);
            }
        }
    }
}
