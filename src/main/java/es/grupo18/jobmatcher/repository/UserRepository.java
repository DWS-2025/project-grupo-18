package es.grupo18.jobmatcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.grupo18.jobmatcher.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
