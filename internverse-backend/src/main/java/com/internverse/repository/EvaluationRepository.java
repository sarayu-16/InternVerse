package com.internverse.repository;

import com.internverse.model.Evaluation;
import com.internverse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    List<Evaluation> findByUser(User user);

    List<Evaluation> findByEvaluator(User evaluator);
}
