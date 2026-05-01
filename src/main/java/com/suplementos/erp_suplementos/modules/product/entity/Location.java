package com.suplementos.erp_suplementos.modules.product.entity;

import com.suplementos.erp_suplementos.modules.product.enums.LocationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private LocationType type;

    // Novos campos de endereço
    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;

    // Contato
    private String phone;
    private String email;

    public Location(Object o, String lojaCentro, LocationType locationType) {
    }
}
