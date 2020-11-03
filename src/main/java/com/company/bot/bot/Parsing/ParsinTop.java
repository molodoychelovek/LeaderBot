package com.company.bot.bot.Parsing;

import com.company.bot.bot.DataBase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;


public class ParsinTop implements Runnable {

    @Override
    public void run() {
        while (true) {
            start();
        }
    }


    private void start() {
        int i = 0;
        WebSurfing ws = new WebSurfing();

        while (true) {
            DataBase db = null;

            try {
                String timebd = LocalDateTime.now(ZoneId.of("Europe/Moscow"))
                        .format(DateTimeFormatter.ofPattern("HH:mm  ·  d.MM.yyyy"));
                System.out.println("time bd in pars: " + timebd);
                db = new DataBase();

                ArrayList arr = db.getParsInfo("main");
                if (arr.size() > 0) {
                    if (i >= arr.size()) {
                        i = 0;
                    }
                    HashMap<String , String> hm = (HashMap<String, String>) arr.get(i);

                    if (hm.get("Ссылка для проверки").contains("http")) {
                        String url = hm.get("Ссылка для проверки");

                        HashMap<String, String> result = ws.connect(url);

                        setStatus(hm.get("Дилер"), result.get("Результат"), url, result.get("Конкурент"), db);
                        i++;
                    }
                    else {
                        i++;
                    }
                } else {
                    try {
                        System.out.println("БД пустая");
                        Thread.sleep(100000);
                    } catch (Exception e){ }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                try{
                    String timebd = LocalDateTime.now(ZoneId.of("Europe/Moscow"))
                            .format(DateTimeFormatter.ofPattern("HH:mm  ·  d.MM.yyyy"));
                    System.out.println("time bd in pars stop: " + timebd);
                    db.close();
                }catch (Exception e){}
            }
        }
    }



    private String setStatus(String url_dealer, String url_top, String url_auto, String competitor, DataBase db) {

        url_dealer = url_dealer.replace("https://auto.ru/diler/cars/new/", "");
        String[] split = url_dealer.split("/");
        url_dealer = split[0];

        System.out.println("Dealer: " + url_dealer);
        System.out.println("Top: " + url_top);


        String time = LocalDateTime.now(ZoneId.of("Europe/Moscow"))
                .format(DateTimeFormatter.ofPattern("HH:mm  ·  d.MM.yyyy"));

        if(url_top.contains(url_dealer)) {
            System.out.println("+");
            db.setResult("+", url_top, url_auto, competitor, time);
        }
        else{
            if(url_top.equals(" ") || url_top.equals("")){
                System.out.println("В топе нет машин");
                db.setResult("В топе нет машин", url_top, url_auto, "", time);
            } else {
                System.out.println("-");

                ArrayList users = db.getParsInfo("main");
                HashMap<String, String> info = new HashMap<>();

                for(int i = 0; i < users.size(); i++){
                    HashMap<String, String > hm = (HashMap<String, String>) users.get(i);

                    if(hm.get("Ссылка для проверки").equals(url_auto)) {
                        info = hm;
                    }
                }

                db.addMain(info.get("Ссылка для проверки"), info.get("Лидер"), info.get("Дилер"), info.get("Марка"),
                        info.get("Модель"), info.get("Комплектация"), info.get("Кузов"), info.get("Привод"),
                        info.get("id поколения"), info.get("id модификации"), info.get("id кузова"), info.get("Наш id дилера"),
                        info.get("VIN на поднятие"), info.get("Ссылка на поднятие"), info.get("Телеграм ид дилера"),
                        info.get("Результат"), info.get("Время"), "check_url", info.get("Чат ид"), competitor);

                db.setResult("-", url_top, url_auto, competitor, time);
            }
        }
        return "null";
    }
}
