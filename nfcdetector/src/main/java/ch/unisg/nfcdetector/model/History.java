package ch.unisg.nfcdetector.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
public class History{
    private int code;
    private Date ts;
}
