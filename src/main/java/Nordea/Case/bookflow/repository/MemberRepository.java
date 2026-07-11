package Nordea.Case.bookflow.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import Nordea.Case.bookflow.domain.Member;

public interface MemberRepository extends ReactiveCrudRepository<Member, Long> {
}