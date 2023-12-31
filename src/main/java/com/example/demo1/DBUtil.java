package com.example.demo1;

import java.sql.*;

public class DBUtil {

    // 注册加载驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private final Connection conn = null;
    private final Statement statement = null;


    public static Connection getConnection(){
        String url ="jdbc:mysql://localhost:3306/userdata" ;
        String user = "root";
        String password = "OVObzd";

        // 获取连接
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    // 登录
    public static boolean login(String username, String password) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            rs = statement.executeQuery();

            // 如果存在匹配的用户记录，表示登录成功
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace(); // 处理异常，或者将异常抛出给调用者
            return false; // 登录失败
        }
    }



    // 注册
    public static int register(String username, String password) {
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO user (username, password) VALUES (?, ?)";
            statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            // 如果插入成功，返回true；否则返回false
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static int select(String username) throws SQLException {
        Connection conn = DBUtil.getConnection();

        String sql = "select * from user where username = ?";

        PreparedStatement statement = conn.prepareStatement(sql);

        statement.setString(1, username);

        int rs = statement.executeUpdate();

        conn.close();
        return rs;
    }
    // 修改密码的逻辑
    public static int updatePassword(String username, String password) {
        Connection conn = DBUtil.getConnection();
        try {
            String sql = "update user set password = ? where username = ?";

            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setString(1,password);
            statement.setString(2,username);

            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 验证用户名已存在
    public static boolean isExist(String username) {
        Connection conn = DBUtil.getConnection();
        try {
            String sql = "select * from user where username = ?";

            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setString(1,username);

            ResultSet rs = null;

            rs = statement.executeQuery();

            return rs.next();
            } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertMessage(String username, String title, String content) throws SQLException {
        Connection conn = DBUtil.getConnection();
        try {
            String sql = "insert into message (username,title,content) Values (?,?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, title);
            statement.setString(3, content);

            statement.executeUpdate();
            System.out.println("文章message:插入成功");
        } catch (SQLException e) {
            String sql = "UPDATE message set  title = ?, content=?  Where username = ? ";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, title);
                statement.setString(2, content);
                statement.setString(3, username);

                statement.executeUpdate();
                System.out.println("文章message:更新成功");
            }catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            conn.close();
        }
    }



    // 通过传入username定位对应的username,以便后续数据写入
    public static String selectUsername(String username) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT username FROM user WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            String name = null;
            while (resultSet.next()) {
                name = resultSet.getString("username");
            }
            return name; // 返回username字段的值
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertMes(String username,String name, String email, String sex, int age){
        Connection connection = DBUtil.getConnection();
        try {

            String sql = "Insert into info (username,name,sex,email,age) values (?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,username);
            statement.setString(2,name);
            statement.setString(3,sex);
            statement.setString(4,email);
            statement.setInt(5,age);

            statement.executeUpdate();
        }catch (SQLException e){
            String sql = "UPDATE info SET name = ?, email = ?, sex = ?, age = ? WHERE username = ?";
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                statement.setString(1,name);
                statement.setString(2,email);
                statement.setString(3,sex);
                statement.setInt(4,age);
                statement.setString(5,username);

                statement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    // 从info中查询username对应的name，sex，email,age
    public static String[] selectMes(String username) {
        Connection connection = DBUtil.getConnection();
        String name = null;
        String sex = null;
        String email = null;
        int age = 0;

        try {
            // Use a proper SELECT query without placeholders for values
            String sql = "SELECT name, sex, email, age FROM info WHERE username=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            // Execute the query
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                // Retrieve data from the result set
                name = rs.getString("name");
                sex = rs.getString("sex");
                email = rs.getString("email");
                age = rs.getInt("age");
            }

            System.out.println("name:" + name + "  sex:" + sex + "  email:" + email + "  age:" + age);
            return new String[]{name, sex, email, String.valueOf(age)};
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
