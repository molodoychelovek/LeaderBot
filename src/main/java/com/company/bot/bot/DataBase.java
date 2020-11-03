package com.company.bot.bot;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class DataBase {
    private Connection con = null;
    private ResultSet rs;
    private Properties pr = new Properties();
    private PreparedStatement st;

    public DataBase(){
        try {
            Class.forName("com.mysql.jdbc.Driver");

            pr.setProperty("user", "root");
            pr.setProperty("password", "");
            pr.setProperty("useUnicode", "true");
            pr.setProperty("useJDBCCompliantTimezoneShift", "true");
            pr.setProperty("useLegacyDatetimeCode", "false");
            pr.setProperty("serverTimezone", "UTC");
            pr.setProperty("characterEncoding", "cp1251");
            pr.setProperty("useSSL", "false");
            pr.setProperty("autoReconnect", "true");

            con = DriverManager.getConnection("jdbc:", pr);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addMain(
            String url, String leader, String dealer, String mark, String model, String complectation, String cuzov,
            String privod, String generation, String tech_param_id, String configuration, String our_dealer, String VIN,
            String url_up, String telegram, String result, String time, String nameTable, String chat_id, String competitor){

        String sql = "";
        sql = "INSERT INTO " + nameTable + "(url, leader, dealer, mark, model, complectation, " +
                "cuzov, privod, generation, tech_param_id, configuration, our_dealer, VIN, url_up, " +
                "telegram, result, time, chat_id, competitor) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            st = con.prepareStatement(sql);
            st.setString(1, url);
            st.setString(2, leader);
            st.setString(3, dealer);
            st.setString(4, mark);
            st.setString(5, model);
            st.setString(6, complectation);
            st.setString(7, cuzov);
            st.setString(8, privod);
            st.setString(9, generation);
            st.setString(10, tech_param_id);
            st.setString(11, configuration);
            st.setString(12, our_dealer);
            st.setString(13, VIN);
            st.setString(14, url_up);
            st.setString(15, telegram);
            st.setString(16, result);
            st.setString(17, time);
            st.setString(18, chat_id);
            st.setString(19, competitor);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void setResult(String result, String leader, String url, String competitor, String time){
        try {
            st = con.prepareStatement("UPDATE main SET result = ? , leader = ?, time = ?, competitor = ? WHERE url = ?");
            st.setString(1, result);
            st.setString(2, leader);
            st.setString(3, time);
            st.setString(4, competitor);
            st.setString(5, url);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setChatID(Long chatID, String id_leader){
        try {
            st = con.prepareStatement("UPDATE main SET chat_id = ? WHERE telegram = ?");
            st.setString(1, String.valueOf(chatID));
            st.setString(2, id_leader);
            System.out.println("+");
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTime(int min){
        try {
            st = con.prepareStatement("UPDATE properties SET time = ?");
            st.setString(1, String.valueOf(min));
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setStop(Long chatID, boolean stop){
        try {
            st = con.prepareStatement("UPDATE main SET stop = ? WHERE chat_id = ?");
            st.setString(2, String.valueOf(chatID));
            if(stop)
                st.setString(1, "+");
            else
                st.setString(1, "-");
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTime(){
        int min = 10;
        try{
            String query = "select * FROM properties";
            st = con.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()){
                min = Integer.valueOf(rs.getString("time"));
            }
            return min;
        }catch (Exception e){
            e.printStackTrace();
        }
        return min;
    }

    public String getStop(String chat_id){
        String stop = "-";
        try{
            String query = "select * FROM main WHERE chat_id = ?";
            st = con.prepareStatement(query);
            st.setString(1, chat_id);
            rs = st.executeQuery();

            while (rs.next()){
               stop = rs.getString("stop");
            }
            return stop;
        }catch (Exception e){
            e.printStackTrace();
        }
        return stop;
    }

    public ArrayList getResults(String chat_id){
        try{
            String query = "select * FROM check_url WHERE result = ? and chat_id = ?";
            st = con.prepareStatement(query);
            st.setString(1, "-");
            st.setString(2, chat_id);
            rs = st.executeQuery();

            ArrayList<String> list = new ArrayList<String>();

            while (rs.next()){
                for(int i = 0; i < list.size(); i++){
                    if(list.get(i).contains(rs.getString("url"))){
                        list.remove(i);
                    }
                }
                list.add("Нужно поднять:" +
                        "\n" + rs.getString("url_up") +
                        "\n\nКонкурент: " + rs.getString("competitor") +
                        "\n\n" + rs.getString("mark") + " " + rs.getString("model") +
                        ", " + rs.getString("cuzov") +"\n" +
                        rs.getString("complectation") +
                        "\n" + "Ссылка на группу:\n" + rs.getString("url") +
                        "\n\nБыло проверено " + rs.getString("time") + "\n\n\n");
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList getParsInfo(String nameTeable){

        ArrayList<HashMap<String, String >> infoArray = new ArrayList<>();


        try{
            String query = "select * FROM " + nameTeable;
            st = con.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()){
                HashMap<String , String> info = new HashMap<String , String>();
                info.put("Ссылка для проверки", rs.getString("url"));
                info.put("Лидер", rs.getString("leader"));
                info.put("Дилер", rs.getString("dealer"));
                info.put("Марка", rs.getString("mark"));
                info.put("Модель", rs.getString("model"));
                info.put("Комплектация", rs.getString("complectation"));
                info.put("Кузов", rs.getString("cuzov"));
                info.put("Привод", rs.getString("privod"));
                info.put("id поколения", rs.getString("generation"));
                info.put("id модификации", rs.getString("tech_param_id"));
                info.put("id кузова", rs.getString("configuration"));
                info.put("Наш id дилера", rs.getString("our_dealer"));
                info.put("VIN на поднятие", rs.getString("VIN"));
                info.put("Ссылка на поднятие", rs.getString("url_up"));
                info.put("Телеграм ид дилера", rs.getString("telegram"));
                info.put("Результат", rs.getString("result"));
                info.put("Время", rs.getString("time"));
                info.put("Чат ид", rs.getString("chat_id"));
                info.put("Конкурент", rs.getString("competitor"));
                infoArray.add(info);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return infoArray;
    }

    public String remove(String user){
        String sql = "DELETE FROM main WHERE telegram = ?";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, user);
            preparedStatement.executeUpdate();
            return "Все записи " + user + " успешно удалены";
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        return "Не удалось удалить записи " + user;
    }

    public String removeAll(String nameTable){
        String sql = "DELETE FROM " + nameTable;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();

            String auto_inc = "ALTER TABLE " + nameTable +  " AUTO_INCREMENT = 0;";

            try {
                System.out.println("REMOVE");
                st = con.prepareStatement(auto_inc);
                st.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }

            return "Все записи успешно удалены";
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        return "Не удалось удалить все записи";
    }


    public void close(){
        try { rs.close(); } catch (Exception e) { /* ignored */ }
        try { st.close(); } catch (Exception e) { /* ignored */ }
        try { con.close(); } catch (Exception e) { /* ignored */ }
    }
}
