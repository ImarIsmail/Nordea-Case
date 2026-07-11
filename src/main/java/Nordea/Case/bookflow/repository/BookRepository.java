package Nordea.Case.bookflow.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import Nordea.Case.bookflow.domain.Book;

public interface BookRepository extends ReactiveCrudRepository<Book, Long> {
}