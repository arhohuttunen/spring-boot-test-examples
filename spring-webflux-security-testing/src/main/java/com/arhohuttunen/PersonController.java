package com.arhohuttunen;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class PersonController {
    private final PersonRepository personRepository;

    @GetMapping("/person/{id}")
    Mono<Person> getPerson(@PathVariable Long id) {
        return personRepository.findById(id);
    }

    @PostMapping("/person")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    Mono<Person> createPerson(@RequestBody Person person) {
        return personRepository.save(person);
    }

    @DeleteMapping("/person/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    Mono<Void> deletePerson(@PathVariable Long id) {
        return personRepository.deleteById(id);
    }
}
