package com.example.storage.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
@Data
@Entity
@Table(name = "FILES")
public class FileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 20)
    private String name;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private Integer size;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserData userId;
    @Column(columnDefinition = "boolean default false")
    private boolean remote; //для ведения статистики добавил эту графу
}
