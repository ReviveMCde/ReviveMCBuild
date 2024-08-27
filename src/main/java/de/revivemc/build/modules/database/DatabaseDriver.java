package de.revivemc.build.modules.database;

import org.bukkit.Bukkit;

import java.sql.*;

public class DatabaseDriver {
    private String HOST = "127.0.0.1";
    private String DATABASE = "mcserver";
    private String USER = "root";
    private String PASSWORD = "password";
    private Connection connection;

    public DatabaseDriver(String host, String database, String user, String password)
    {
        this.HOST = host;
        this.DATABASE = database;
        this.USER = user;
        this.PASSWORD = password;

        connect();
    }

    public void connect()
    {
        try
        {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.HOST + ":3306/" + this.DATABASE + "?autoReconnect=true", this.USER, this.PASSWORD);
            Bukkit.getConsoleSender().sendMessage("Connected to MySQL!");
        }
        catch (SQLException e)
        {
            Bukkit.getConsoleSender().sendMessage("Errow while connecting to MySQL! Error: " + e.getMessage());
        }
    }

    public void close()
    {
        try
        {
            if (this.connection != null)
            {
                this.connection.close();
                Bukkit.getConsoleSender().sendMessage("Connection to MySQL was stopped!");
            }
        }
        catch (SQLException e)
        {
            Bukkit.getConsoleSender().sendMessage("Error while stopping MySQL! Error: " + e.getMessage());
        }
    }

    public void update(String sql)
    {
        try
        {
            this.connection.createStatement().executeUpdate(sql);
        }
        catch (SQLException ignored) {

        }
    }

    public ResultSet query(String qry) {
        ResultSet resultSet = null;
        try
        {
            Statement statement = this.connection.createStatement();
            resultSet = statement.executeQuery(qry);
        }
        catch (SQLException exception)
        {
            connect();
            System.err.println(exception);
        }
        return resultSet;
    }
}
