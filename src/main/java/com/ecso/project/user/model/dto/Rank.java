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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "RANK")
public class Rank {
	
	@Id
    @Column(name = "RANK_NO")
    private Long rankNo;

    @Column(name = "RANK_TITLE", length = 10, nullable = false)
    private String rankTitle;

    
}
