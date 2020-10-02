package com.ittopdogs.rabobank.model;

import lombok.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@XmlRootElement(name = "record")
public class Transaction implements Serializable {
    private int id;
    @Getter(AccessLevel.NONE)
    private String reference;
    private String accountNumber;
    private String description;
    private BigDecimal startBalance;
    private BigDecimal mutation;
    private BigDecimal endBalance;

    @XmlAttribute
    public String getReference() {
        return reference;
    }
}
