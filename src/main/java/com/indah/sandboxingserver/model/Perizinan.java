package com.indah.sandboxingserver.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.util.Date;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "perizinan")
public class Perizinan {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id", nullable = false)
    private User user;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date tanggal;

    @ManyToOne
    @JoinColumn(name="id_data", referencedColumnName = "id")
    private KatalogData data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPerizinan status;

    public Perizinan(User user, KatalogData kd) {
        this.user = user;
        this.data = kd;
        this.tanggal = new Date();
        this.status = StatusPerizinan.PENDING;
    }

    @PrePersist
    private void generateId() {
        if (this.id == null || this.id.isEmpty()) {
            String prefix = "REQ";
            String timestamp = String.valueOf(System.currentTimeMillis());
            String random = String.valueOf(new Random().nextInt(10)); // Adjust the range as needed

            this.id = prefix + timestamp + random;
        }
    }
}