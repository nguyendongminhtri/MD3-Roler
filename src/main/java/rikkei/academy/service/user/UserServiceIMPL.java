package rikkei.academy.service.user;

import rikkei.academy.config.ConnectMySQL;
import rikkei.academy.model.Role;
import rikkei.academy.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserServiceIMPL implements IUserService{
    private Connection connection = ConnectMySQL.getConnection();
    private final String CREATE_USER = "INSERT INTO users(name,username,email,password) values (?,?,?,?);";
    private final String INSERT_ROLE_ID_USER_ID = "INSERT INTO user_role(user_id,role_id) values (?,?);";
    private final String FIND_ALL_USERNAME = "SELECT username FROM users";
    private final String FIND_ALL_EMAIL = "SELECT email FROM users";
    @Override
    public void save(User user) {
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2,user.getUsername());
            preparedStatement.setString(3,user.getEmail());
            preparedStatement.setString(4,user.getPassword());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            int user_id = 0; //LAY RA ID CUAR USER DE TAO VAO BANG CHUNG GIAN
            while (resultSet.next()){
                user_id = resultSet.getInt(1);
            }
            //TAO DU LIEU CHO BANG TRUNG GIAN
            PreparedStatement preparedStatement1 = connection.prepareStatement(INSERT_ROLE_ID_USER_ID);
            Set<Role> roles = user.getRoles();
            List<Role> roleList = new ArrayList<>(roles); // Convert tu Set -> List
            List<Integer> listRoleId = new ArrayList<>();
            for (int i = 0; i < roleList.size(); i++) {
                listRoleId.add(roleList.get(i).getId());
            }
            for (int i = 0; i < listRoleId.size(); i++) {
                preparedStatement1.setInt(1,user_id);
                preparedStatement1.setInt(2,listRoleId.get(i));
                preparedStatement1.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existedByUsername(String username) {
        List<String> listUsername = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_USERNAME);
           ResultSet resultSet =  preparedStatement.executeQuery();
           while (resultSet.next()){
               listUsername.add(resultSet.getString("username"));
           }
            for (int i = 0; i < listUsername.size(); i++) {
                if(username.equals(listUsername.get(i))){
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean existedByEmail(String email) {
        List<String> listEmail = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_EMAIL);
            ResultSet resultSet =  preparedStatement.executeQuery();
            while (resultSet.next()){
                listEmail.add(resultSet.getString("email"));
            }
            for (int i = 0; i < listEmail.size(); i++) {
                if(email.equals(listEmail.get(i))){
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
