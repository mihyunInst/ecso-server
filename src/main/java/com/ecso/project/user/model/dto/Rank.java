package com.ecso.project.user.model.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "RANK")
public class Rank {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "RANK_NO")
    private Long rankNo;

    @Column(name = "RANK_TITLE", length = 10, nullable = false)
    private String rankTitle;

    @OneToMany(mappedBy = "rank")
    private List<User> users = new ArrayList<>();
    
}
