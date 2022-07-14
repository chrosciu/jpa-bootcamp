package com.chrosciu.listener;

import com.chrosciu.domain.Team;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TeamListener {

    @PrePersist
    public void prePersist(Team team) {
        if ("Wajchowi".equals(team.getName())) {
            team.setName("Magicy");
        }
    }

    @PostPersist
    public void postPersist(Team team) {
        if ("Czarodzieje".equals(team.getName())) {
            throw new IllegalArgumentException("Takich tu nie chcemy!");
        }
    }

}
