package gur.bot.repository;

import gur.bot.domain.model.SomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends JpaRepository<SomeEntity, Long> {
}
