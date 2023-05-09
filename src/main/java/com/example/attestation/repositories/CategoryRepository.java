package com.example.attestation.repositories;

import com.example.attestation.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    com.example.attestation.models.Category findByName (String name);

}
