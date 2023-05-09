package com.example.attestation.repositories;

import com.example.attestation.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    //Позволяет найти запись в сущности Person по логину
    Optional<Person> findByLogin (String login);
}
