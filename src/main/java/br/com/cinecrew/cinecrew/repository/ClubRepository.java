package br.com.cinecrew.cinecrew.repository;

import br.com.cinecrew.cinecrew.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long> {

    List<Club> findByOwnerId(Long ownerId);
}
