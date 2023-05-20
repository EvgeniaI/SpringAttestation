package com.example.attestation.repositories;

import com.example.attestation.enumm.Status;
import com.example.attestation.models.Order;
import com.example.attestation.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    //получаем лист всех заказов конкретного пользователя
    List<Order> findByPerson(Person person);

    //поиск по последним цифрам/буквам номера заказа
    List<Order> findByNumberEndingWith(String endingWith);

    Order findByNumber(String number);
}
