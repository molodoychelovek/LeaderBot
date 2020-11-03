package com.company.bot.bot;

import com.company.bot.bot.FilesManager.ExcelReader;
import com.company.bot.bot.FilesManager.JsonFiles;
import com.company.bot.bot.Keyboards.KeyBoards;
import com.company.bot.bot.Parsing.ParsinTop;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Math.toIntExact;

public class Bot extends TelegramLongPollingBot {
    Thread parsing = new Thread(new ParsinTop());
    Thread check = new Thread(new CheckStatus());
    private Update update = null;

    @Override
    public void onUpdateReceived(Update update) {
        String timebd = LocalDateTime.now(ZoneId.of("Europe/Moscow"))
                .format(DateTimeFormatter.ofPattern("HH:mm  ·  d.MM.yyyy"));
        DataBase db = new DataBase();
        System.out.println("DB start in Bot: " + timebd);


        try {
            this.update = update;
            if(!update.hasCallbackQuery()) {
                String chat_id = String.valueOf(update.getMessage().getChatId());

                update.getUpdateId();

                try {
                    System.out.println(update.getMessage().getFrom().getUserName() + ": " + update.getMessage().getText());
                }
                catch (Exception e){
                    System.out.println(update.getMessage().getFrom().getUserName() + ": file");
                }

                SendMessage sendMessage = new SendMessage().setChatId(chat_id);
                sendMessage.setReplyMarkup(new KeyBoards(update));
                sendMessage.setText(message(update, db));

                execute(sendMessage);
            } else if(update.hasCallbackQuery()){
                String call_data = update.getCallbackQuery().getData();
                long message_id = update.getCallbackQuery().getMessage().getMessageId();
                long chat_id = update.getCallbackQuery().getMessage().getChatId();

                EditMessageText new_message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id))
                        .setReplyMarkup(new KeyBoards(update));

                if(call_data.equals("Уведомления")){
                    new_message.setText("Что делать с уведомлениями");
                }
                if(call_data.equals("Назад_Ув") || call_data.equals("Назад_Уд")){
                    new_message.setText("Для начала работы перетащите сюда файл для ЛидерБота.");
                }
                if(call_data.equals("Выключить")){
                    db.setStop(chat_id, true);
                    new_message.setText("Уведомления выключены. Выберете пункт меню");
                }
                if(call_data.equals("Включить")){
                    db.setStop(chat_id, false);
                    new_message.setText("Уведомления включены. Выберете пункт меню");
                }
                if(call_data.equals("Удалить")){
                    new_message.setText("Что вы собироаетесь удалять?");
                }
                if(call_data.equals("Мои записи") || call_data.equals("Все записи")){
                    new_message.setText("Вы уверены что хотите удалить записи?");
                }
                if(call_data.equals("Нет_М") || call_data.equals("Нет_В")){
                    new_message.setText("Для начала работы перетащите сюда файл для ЛидерБота.");
                }
                if(call_data.equals("Да_М")){
                    db.remove(update.getCallbackQuery().getFrom().getUserName());
                    new_message.setText("Вы удалили все свои записи");
                }
                if(call_data.equals("Да_В")){
                    db.removeAll("main");
                    db.removeAll("check_url");
                    new_message.setText("Вы удалили записи всех пользователей");
                }

                if(call_data.equals("Получить файл")){
                    ExcelReader er = new ExcelReader();
                    SendMessage sendMessage = new SendMessage()
                            .setChatId(chat_id);
                    try {
                        DeleteMessage deleteMessage = new DeleteMessage()
                                .setChatId(chat_id)
                                .setMessageId(toIntExact(message_id));
                        execute(deleteMessage);

                        String time = LocalDateTime.now(ZoneId.of("Europe/Moscow"))
                                .format(DateTimeFormatter.ofPattern("HH:mm  ·  d.MM.yyyy"));
                        SendDocument sendDocument = new SendDocument()
                                .setChatId(chat_id)
                                .setDocument("export.xlsx", er.getFile())
                                .setCaption(time);
                        execute(sendDocument);

                        db.setChatID(chat_id, update.getCallbackQuery().getMessage().getChat().getUserName());
                        sendMessage.setText("Для начала работы перетащите сюда файл для ЛидерБота.");
                    } catch (Exception e){
                        sendMessage.setText("Не удалось выгрузить файл, либо БД пустая. Выберите пункт меню");
                        e.printStackTrace();
                    }
                    sendMessage.setReplyMarkup(new KeyBoards(update));
                    execute(sendMessage);
                }

                if(!call_data.equals("Получить файл"))
                    execute(new_message);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                String timebdclose = LocalDateTime.now(ZoneId.of("Europe/Moscow"))
                        .format(DateTimeFormatter.ofPattern("HH:mm  ·  d.MM.yyyy"));
                System.out.println("DB close in Bot" + timebdclose);
                db.close();
            } catch (Exception e){

            }
        }

    }


    public String message(Update update, DataBase db){
        try {
            if(!isFile(update) && !update.hasCallbackQuery()) {
                if (update.getMessage().getText().equals("/pars")) {
                    if(parsing.isInterrupted() || !parsing.isAlive()) {
                        if(db.getParsInfo("main").size() > 0) {
                            parsing.start();
                            if(check.isInterrupted() || !check.isAlive())
                                check.start();
                            return "Сверяем ссылки, ожидайте.";
                        }
                        else{
                            return "Загрузите файл с данными для парсинга";
                        }
                    }
                    return "Парсинг уже запущен.";
                }
                if (update.getMessage().getText().equals("/start")){
                    db.setChatID(update.getMessage().getChatId(), update.getMessage().getChat().getUserName());
                    return "Для начала работы перетащите сюда файл для ЛидерБота.";
                }
                if (update.getMessage().getText().contains("/time")){
                    String text = update.getMessage().getText();
                    int time = Integer.valueOf(text.replace("/time ", ""));
                    db.setTime(time);
                    return  "Время установлено";
                }
                else {
                    return "Если у вас возникли проблемы, пропишите /start";
                }
            }
            else {
                db.remove(update.getMessage().getChat().getUserName());
                File file = new JsonFiles().getFile(getBotToken(), update); // Download file
                String res = new ExcelReader().uploadFile(file); // reading excel file
                db.setChatID(update.getMessage().getChatId(), update.getMessage().getChat().getUserName());

                if(parsing.isInterrupted() || !parsing.isAlive() || Thread.currentThread().isInterrupted()) {
                    parsing.start();
                    check.start();
                }

                return res;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return "Если у вас возникла ошибка отправьте команду /start";
    }

    private boolean isFile(Update update) {
        return !update.getMessage().hasText() ||
                (update.getMessage().hasText() && update.getMessage().hasDocument())
                ? true : false;
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken() {
        return "";
    }



    class CheckStatus implements Runnable{

        @Override
        public void run() {
            start();
        }

        private void start(){
            while(true) {
                DataBase db = new DataBase();

                try {
                    Thread.sleep(db.getTime() * 60000);
                    System.out.println("mls: " + db.getTime() * 60000);
                } catch (Exception e){
                    e.printStackTrace();
                }

                ArrayList arr = db.getParsInfo("check_url");
                ArrayList<String> users = new ArrayList<>();

                for(int i = 0; i < arr.size(); i++){
                    HashMap<String, String> hm = (HashMap<String, String>) arr.get(i);
                    if(db.getStop(hm.get("Чат ид")).equals("-"))
                        users.add(hm.get("Чат ид"));
                }

                Set<String> set = new HashSet<>(users);
                users.clear();
                users.addAll(set);


                for(int i = 0; i < users.size(); i++){
                    try {
                        SendMessage sendMessage = new SendMessage().setChatId(Long.valueOf(users.get(i)));
                        sendMessage.disableWebPagePreview();

                        ArrayList<String> str = db.getResults(users.get(i));
                        int size = 0;
                        for(String s : str){
                            size += s.length();
                        }

                        if(size > 4000){
                            String newStr = "";

                            for(int n = 0; n < str.size(); n++){
                                if((newStr += str.get(n)).length() < 3000){
                                    newStr += str.get(n);
                                } else {
                                    sendMessage.setText(newStr);
                                    if(n == str.size() - 1)
                                        sendMessage.setReplyMarkup(new KeyBoards(update));
                                    execute(sendMessage);
                                    newStr = "";
                                }
                            }
                        }else{
                            String newStr = "";
                            for(String s : str){
                                newStr += s;
                            }
                            sendMessage.setText(newStr);
                            sendMessage.setReplyMarkup(new KeyBoards(update));
                            execute(sendMessage);
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                db.removeAll("check_url");
                db.close();
            }
        }
    }
}
