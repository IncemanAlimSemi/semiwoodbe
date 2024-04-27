package com.alseinn.semiwood.dao.working;

import com.alseinn.semiwood.entity.working.WorkingHours;
import com.alseinn.semiwood.entity.working.enums.Day;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {
    Optional<WorkingHours> findByDay(Day day);
}
