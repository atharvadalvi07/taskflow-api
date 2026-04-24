package com.taskflow.taskflow_api.repository;

import com.taskflow.taskflow_api.model.Task;
import com.taskflow.taskflow_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByOwner(User owner);

    List<Task> findAllByOwner(User owner);

    Optional<Task> findByIdAndOwner(Long id, User owner);
}