package org.ibrahimhammani.securedb.idbeforeinsert.repository;

import org.ibrahimhammani.securedb.idbeforeinsert.IdBeforeInsertApplication;
import org.ibrahimhammani.securedb.idbeforeinsert.entity.Odd;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { IdBeforeInsertApplication.class })
class OddRepositoryTest {
    @Autowired
    OddRepository oddRepository;

    @Test
    public void saveTest(){
        Odd savedOdd1 = oddRepository.save(new Odd());
        assertFalse(savedOdd1.getIsOdd());
        Odd savedOdd2 = oddRepository.save(new Odd());
        assertTrue(savedOdd2.getIsOdd());
    }

}