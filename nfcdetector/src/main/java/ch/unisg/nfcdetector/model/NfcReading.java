package ch.unisg.nfcdetector.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
public class NfcReading {

    private ArrayList<History> history;
    private Date ts;
    private Workpiece workpiece;

}
