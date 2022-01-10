package com.example.graphql.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.graphql.entity.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person,Integer>{

	Person findByEmail(String email);

}
