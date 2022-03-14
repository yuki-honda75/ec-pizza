package com.example.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.User;

/**
 * 
 * @author hondayuki
 *
 */
@Repository
public class UserRepository {
	
	@Autowired
	private NamedParameterJdbcTemplate template;
	
	private static final RowMapper<User> USER_ROW_MAPPER = new BeanPropertyRowMapper<>(User.class);
	
	/**
	 * ユーザ情報をインサート
	 * 
	 * @param user
	 */
	public void insert(User user) {
		String sql = "INSERT INTO users"
				+ " ( name, email, password, zipcode, address, telephone) VALUES"
				+ " (:name,:email,:password,:zipcode,:address,:telephone)";
		SqlParameterSource param = new MapSqlParameterSource()
									.addValue("name", user.getName())
									.addValue("email", user.getEmail())
									.addValue("password", user.getPassword())
									.addValue("zipcode", user.getZipcode())
									.addValue("address", user.getAddress())
									.addValue("telephone", user.getTelephone());
		
		template.update(sql, param);
	}
	
	/**
	 * メールアドレス検索
	 * 
	 * @param email
	 * @return なければnull
	 */
	public User findByMailAddress(String email) {
		String sql = "SELECT id,email,password FROM users WHERE email=:email";
		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);
		List<User> userList = template.query(sql, param, USER_ROW_MAPPER);
		
		if (userList.size() == 0) {
			return null;
		}
		return userList.get(0);
	}
}
