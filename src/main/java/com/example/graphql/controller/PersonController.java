package com.example.graphql.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.graphql.entity.Person;
import com.example.graphql.repository.PersonRepository;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	private PersonRepository personRepository;

	@Value("classpath:person.graphqls")
	private Resource schemaResource;

	private GraphQL graphQL;

	@PostConstruct
	public void loadSchema() throws IOException {
		File schemaFile = schemaResource.getFile();
		TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
		RuntimeWiring wiring = buildwiring();
		GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
		graphQL = GraphQL.newGraphQL(schema).build();
	}

	private RuntimeWiring buildwiring() {
		DataFetcher<List<Person>> fetcher1 = data -> {
			return (List<Person>) personRepository.findAll();
		};
		DataFetcher<Person> fetcher2 = data -> {
			Person p=personRepository.findByEmail(data.getArgument("email"));
			return p;
		};
		return RuntimeWiring.newRuntimeWiring().type("Query",
				typeWriting -> typeWriting.dataFetcher("getAllPerson", fetcher1).dataFetcher("findPerson", fetcher2))
				.build();
	}

	@PostMapping("/addPerson")
	public String addPerson(@RequestBody Person person) {
		personRepository.save(person);
		return "saved";
	}

	@GetMapping("/findAll")
	public List<Person> findAll() {
		return (List<Person>) personRepository.findAll();
	}

	@PostMapping("/getall")
	public ResponseEntity<Object> getAll(@RequestBody String query) {
		ExecutionResult result = graphQL.execute(query);
		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}
	
	@PostMapping("/getpersonbyemail")
	public ResponseEntity<Object> getPersonByEmail(@RequestBody String query) {
		ExecutionResult result = graphQL.execute(query);
		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}
}
