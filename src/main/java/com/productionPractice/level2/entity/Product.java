package com.productionPractice.level2.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@ToString(exclude = "category")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long productId;

    @Column(nullable = false,length = 25,unique = true)
    private String productName;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false,length = 50)
    private String description;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch=FetchType.LAZY,optional = false)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;
}
