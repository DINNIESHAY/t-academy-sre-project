package ru.itis.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trip_applications")
@Data
@NoArgsConstructor
public class TripApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    private String message;

    @Column(nullable = false)
    private String contacts;

    public TripApplication(User applicant, Trip trip, String message, String contacts) {
        this.applicant = applicant;
        this.trip = trip;
        this.message = message;
        this.contacts = contacts;
    }
}