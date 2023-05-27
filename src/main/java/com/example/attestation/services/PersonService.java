package com.example.attestation.services;

import com.example.attestation.models.Order;
import com.example.attestation.models.Person;
import com.example.attestation.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Person findByLogin(Person person){
        Optional<Person> person_db = personRepository.findByLogin(person.getLogin());
        return person_db.orElse(null);
    }



    @Transactional
    public void register(Person person){
        person.setPassword(passwordEncoder.encode(person.getPassword())); //при записи пароля преобразовываем его в защищенный вид
        person.setRole("ROLE_USER");
        personRepository.save(person);
    }

    public List<Person> getAllPerson(){
        return personRepository.findAll();
    }

    //обновление данных о пользователе по id
    @Transactional
    public void updatePerson(int id, Person person){
        person.setId(id);
        personRepository.save(person);
    }

    public Person getPersonId(int id){
        Optional<Person> optionalPerson = personRepository.findById(id);
        return optionalPerson.orElse(null);
    }
}
