package com.phil.playground.todo

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.transaction.Transactional

@Entity
data class Todo (
        var text: String,
        var done: Boolean = false,
        @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0)

interface TodoRepository: JpaRepository<Todo, Long> {
    @Transactional fun deleteByDone(done: Boolean)
}