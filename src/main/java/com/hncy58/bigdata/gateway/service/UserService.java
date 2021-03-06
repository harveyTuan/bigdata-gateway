package com.hncy58.bigdata.gateway.service;

import java.util.List;

import com.github.pagehelper.Page;
import com.hncy58.bigdata.gateway.exception.RestfulJsonException;
import com.hncy58.bigdata.gateway.model.User;

public interface UserService {

	User selectByPrimaryKey(int id);

	List<User> selectAll();

	Page<User> selectAll(int pageNo, int pageSize, User queryUser);

	int deleteByPrimaryKey(int id);
	
	int delete(List<String> ids);

	int insert(User user);

	int updateByPrimaryKeySelective(User user);

	int updateByPrimaryKey(User user);

	User selectByUserCode(String userCode);

	int linkRole(String userId, List<String> roleIds);

	int unLinkRole(String userId, List<String> roleIds) throws RestfulJsonException;
	
}
