package com.javarush.jira.bugtracking.task;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_tag")
@Data
@NoArgsConstructor
public class Tag {

    @Id
    @Column(name = "task_id")
    private Long taskId;
    private String tag;

}
