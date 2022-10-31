package org.ibrahimhammani.securedb.idbeforeinsert.repository;

import org.ibrahimhammani.securedb.idbeforeinsert.entity.Odd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OddRepository extends JpaRepository<Odd, Long> {
}
