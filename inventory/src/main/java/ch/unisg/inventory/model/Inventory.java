package ch.unisg.inventory.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter @Setter
public class Inventory {

    private Stock stock;

}
