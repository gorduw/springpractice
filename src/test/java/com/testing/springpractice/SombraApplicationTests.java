package com.testing.springpractice;

import com.testing.springpractice.dto.AdvisorDTO;
import com.testing.springpractice.mapper.AdvisorToDtoMapper;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SombraApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void shouldMapAdvisorEntityToAdvisorDTO() {

        AdvisorEntity advisorEntity = new AdvisorEntity();
        advisorEntity.setId(1L);
        advisorEntity.setName("John Dick");
        advisorEntity.setAge(30);

        AdvisorDTO advisorDTO = AdvisorToDtoMapper.INSTANCE.advisorToAdvisorDTO(advisorEntity);

        System.out.println(advisorDTO.toString());
        System.out.println(advisorEntity.toString());

        assertEquals(advisorDTO.getAdvisorId(), advisorEntity.getId());
        assertEquals(advisorDTO.getName(), advisorEntity.getName());
        assertEquals(advisorDTO.getAge(), advisorEntity.getAge());


    }

}
