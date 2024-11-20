package com.ecso.project.user.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USER")
public class User {
	
    @Id // 기본키 지정
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // 시퀀스 사용
    @Column(name = "USER_NO")
    private Long userNo;

    @Column(name = "USER_NAME", length = 10, nullable = false)
    private String userName;

    @Column(name = "USER_EMAIL", length = 50, nullable = false)
    private String userEmail;

    @Column(name = "USER_PW", length = 100, nullable = false)
    private String userPw;

    @Column(name = "USER_PHONE", length = 11, nullable = false)
    private String userPhone;

    @Column(name = "USER_ROLE", length = 1, nullable = false)
    private String userRole = "S";

    @Column(name = "ENROLL_DATE", nullable = false)
    private LocalDateTime enrollDate;

    @Column(name = "CLASS_TERM", length = 5, nullable = false)
    private String classTerm;

    @Column(name = "ADOPTION_COUNT", nullable = false)
    private Integer adoptionCount = 0;

    // 다른 엔티티와의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RANK_NO") // 외래키 컬럼명
    private Rank rank;  // 작성자

    //@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    //private List<Post> posts = new ArrayList<>();
}