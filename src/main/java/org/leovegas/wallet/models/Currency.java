package org.leovegas.wallet.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *  @author Alireza Gholamzadeh Lahroodi
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Currency {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    private LocalDateTime lastUpdated;

    private String lastUpdatedBy;

}
