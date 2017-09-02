package com.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.model.Profile;

@Component
public class SQL {
	@Autowired
	  JdbcTemplate jdbcTemplate;

    public List<Profile> profileQuery(String sql) {
    List<Profile> users = jdbcTemplate.query(sql, new UserMapper());
    return users;
    }
	
    class UserMapper implements RowMapper<Profile> {
    	  public Profile mapRow(ResultSet rs, int arg1) throws SQLException {
    	    Profile profile = new Profile();
    	    profile.setEmail(rs.getString("email"));
    	    profile.setFirstName(rs.getString("firstname"));
    	    profile.setLastName(rs.getString("lastname"));
    	    profile.setEmail(rs.getString("email"));
    	    return profile;
    	  }
    		
    		
    	}
    
}
