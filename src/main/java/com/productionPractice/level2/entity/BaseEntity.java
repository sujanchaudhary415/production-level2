package com.productionPractice.level2.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(nullable = false,updatable = false)
    public Instant createdAt;
    @Column(nullable = false)
    public Instant updatedAt;

   @PrePersist
    protected void onCreate(){
        Instant now=Instant.now();
        createdAt=now;
        updatedAt=now;
    }

    @PreUpdate
    protected void onUpdate()
    {
        updatedAt=Instant.now();
    }
}
