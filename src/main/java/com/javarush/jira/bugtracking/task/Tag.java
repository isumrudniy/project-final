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
    @Column(name = "TASK_ID")
    private Long taskId;
    private String tag;

}
