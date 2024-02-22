package com.engeto.registrationsystem.service;


import com.engeto.registrationsystem.exception.RegistrationException;
import com.engeto.registrationsystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public User createNewUser(User user) throws RegistrationException {
        jdbcTemplate.update("INSERT into user (name, surname, personId, uuid) VALUES (?, ?, ?, ?)",
                user.getName(), user.getSurname(), getNewPersonId(), getNewUuid());
        return user;
    }
    public static List<String> loadFromFile(String filename) throws RegistrationException {
        List<String> result = new ArrayList<>();
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(filename)))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new RegistrationException("Nepodařilo se nalézt soubor " + filename + ": " + e.getLocalizedMessage());
        }
        return result;
    }

    private List<String> getPersonIdList() throws RegistrationException {
        List<String> personIdList = loadFromFile("dataPersonId.txt");
        return personIdList;
    }

//    private String getPersonId(int i) throws RegistrationException {
//        List<String> personIdList = loadFromFile("dataPersonId.txt");
//        return personIdList.get(i);
//    }

    public String getNewPersonId() throws RegistrationException {
        int index = 0;
        List<String> personIdList = getPersonIdList();
        String result = personIdList.get(index);
        List <String> personIdFromData = jdbcTemplate.query("SELECT personId FROM user", new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("personId");
            }
        });
        while (personIdFromData.contains(result)){
            if (index==personIdList.size()){
                throw new IndexOutOfBoundsException("Seznam person ID se vyčerpal.");
            }
            index++;
            result = personIdList.get(index);
        }
        return result;
    }

    public String getNewUuid() {
        List <String> uuidFromData = jdbcTemplate.query("SELECT uuid FROM user", new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("uuid");
            }
        });
        String uuid = getUuid();
        while (uuidFromData.contains(uuid)){
            uuid = getUuid();
        }
        return uuid;
    }

        private String getUuid(){
            String uuid = UUID.randomUUID().toString();
            return uuid;
        }

    public User getAllInfoOfUserById(Long id) {
        String sql = "SELECT * FROM user WHERE id = " + id;
        User user = (User) jdbcTemplate.queryForObject(sql,  new RowMapper <Object>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setPersonId(rs.getString("personId"));
                user.setUuid(rs.getString("uuid"));
                return user;
            }
        });
        return user;
    }

    public User getShortInfoOfUserById(Long id) {
        String sql = "SELECT * FROM user WHERE id = " + id;
        User user = (User) jdbcTemplate.queryForObject(sql,  new RowMapper <Object>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                return user;
            }
        });
        return user;
    }

    public void checkId(Long id) throws RegistrationException {
        List <Long> dataId = jdbcTemplate.query("SELECT id FROM user" , new RowMapper<Long>(){
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("id");
            }
        });
        if (!dataId.contains(id)){
            throw new RegistrationException("Databáze neobsahuje uživatele s ID: " + id);
        }
    }

    public List<User> getAllInfoOfUsers() {
        List<User> users = jdbcTemplate.query("SELECT * FROM user", new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setPersonId(rs.getString("personId"));
                user.setUuid(rs.getString("uuid"));
                return user;
            }
        });
        return users;
    }

    public List<User> getShortInfoOfUsers() {
        List<User> users = jdbcTemplate.query("SELECT * FROM user", new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                return user;
            }
        });
        return users;
    }

    public void updateUser(User user) {
        jdbcTemplate.update("UPDATE user SET name = ?, surname = ? WHERE id = ?",
                user.getName(), user.getSurname(), user.getId());
    }

    public String checkUpdateUser(User user) {
        User userSQL = getAllInfoOfUserById(user.getId());
        String result;
        if (userSQL.getName().equals(user.getName()) && userSQL.getSurname().equals(user.getSurname())) {
            result = ("Data uživatele se aktualizovala na: \n" + user);
        }
        else result = ("Data uživatele s ID " + user.getId() + " se neaktualizovala!");
        return result;
    }

    public void deleteUserById(Long id) {
        jdbcTemplate.update("DELETE FROM user WHERE id = " + id);
    }

    public String checkDeleteUser(Long id) {
        String result;
        List <Long> dataId = jdbcTemplate.query("SELECT id FROM user" , new RowMapper<Long>(){
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("id");
            }
        });
        if (!dataId.contains(id)){
            result = ("Uživatel s ID " + id + " byl smazán.");
        }
        else result = ("Uživatele se nepodařilo smazat!");
        return result;
    }
    
}
