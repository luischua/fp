package business;

import lombok.Getter;
import lombok.Setter;
import org.lightcouch.Document;

import java.math.BigDecimal;

@Getter
@Setter
public class Product extends Document {
    private String name;
    private BigDecimal price;
}
