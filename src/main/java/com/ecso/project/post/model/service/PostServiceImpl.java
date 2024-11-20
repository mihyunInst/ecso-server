package com.ecso.project.post.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PostServiceImpl implements PostService{
	
}
