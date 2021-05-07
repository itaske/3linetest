package com.threeline.ng.cardverifier.repositories;

import com.threeline.ng.cardverifier.models.Hit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HitRepository extends JpaRepository<Hit, Integer> {
    Optional<Hit> findByCardId(Integer integer);
}
