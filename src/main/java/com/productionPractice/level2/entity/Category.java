package com.productionPractice.level2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "products")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long categoryId;

    @Column(nullable = false,unique = true, length = 100)
    private String categoryName;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Product> products=new ArrayList<>();
}
