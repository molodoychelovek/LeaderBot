package com.company.bot.bot.Keyboards;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyBoards extends InlineKeyboardMarkup{
    private static List listZERO = Arrays.asList(new String[]{"Уведомления", "Получить файл", "Удалить"});
    private static List listA = Arrays.asList(new String[]{"Включить", "Выключить", "Назад"});
    private static List listC = Arrays.asList(new String[]{"Мои записи", "Все записи", "Назад"});

    public KeyBoards(Update update) {
        this.setKeyboard(keyboards(update));
    }

    private List<InlineKeyboardButton> keyboardFirstRow = new ArrayList<>();
    public List<List<InlineKeyboardButton>> keyboards(Update update) {
        InlineKeyboardButton buttonFirst = new InlineKeyboardButton();
        InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
        InlineKeyboardButton buttonThree = new InlineKeyboardButton();

        if(!update.hasCallbackQuery()){
            buttonFirst.setText((String) listZERO.get(0)).setCallbackData((String) listZERO.get(0));
            buttonSecond.setText((String) listZERO.get(1)).setCallbackData((String) listZERO.get(1));
            buttonThree.setText((String) listZERO.get(2)).setCallbackData((String) listZERO.get(2));

            keyboardFirstRow.add(buttonFirst);
            keyboardFirstRow.add(buttonSecond);
            keyboardFirstRow.add(buttonThree);
        }

        else if(update.hasCallbackQuery()) {
            String msg = update.getCallbackQuery().getData();

            if(msg.equals("Назад_Ув") || msg.equals("Получить файл") || msg.equals("Назад_Уд") ||
                    msg.equals("Нет_В") || msg.equals("Нет_М")){
                buttonFirst.setText((String) listZERO.get(0)).setCallbackData((String) listZERO.get(0));
                buttonSecond.setText((String) listZERO.get(1)).setCallbackData((String) listZERO.get(1));
                buttonThree.setText((String) listZERO.get(2)).setCallbackData((String) listZERO.get(2));

                keyboardFirstRow.add(buttonFirst);
                keyboardFirstRow.add(buttonSecond);
                keyboardFirstRow.add(buttonThree);
            }
            if (msg.equals("Уведомления") || msg.equals("Включить") || msg.equals("Выключить")) {
                buttonFirst.setText((String) listA.get(0)).setCallbackData("Включить");
                buttonSecond.setText((String) listA.get(1)).setCallbackData("Выключить");
                buttonThree.setText((String) listA.get(2)).setCallbackData("Назад_Ув");

                keyboardFirstRow.add(buttonFirst);
                keyboardFirstRow.add(buttonSecond);
                keyboardFirstRow.add(buttonThree);
            }
            if(msg.equals("Удалить") || msg.equals("Да_В") || msg.equals("Да_М")){
                String username = update.getCallbackQuery().getMessage().getChat().getUserName();

                buttonFirst.setText((String) listC.get(0)).setCallbackData("Мои записи");
                if(username.equals("roggic") || username.equals("anton_ho"))
                    buttonSecond.setText((String) listC.get(1)).setCallbackData("Все записи");
                buttonThree.setText((String) listC.get(2)).setCallbackData("Назад_Уд");

                keyboardFirstRow.add(buttonFirst);
                if(username.equals("anton_ho"))
                    keyboardFirstRow.add(buttonSecond);
                keyboardFirstRow.add(buttonThree);
            }

            if(msg.equals("Все записи") || msg.equals("Мои записи")){
                if(msg.equals("Все записи")){
                    buttonFirst.setText("Да").setCallbackData("Да_В");
                    buttonSecond.setText("Нет").setCallbackData("Нет_В");
                    buttonThree.setText("Назад").setCallbackData("Назад_Уд");

                    keyboardFirstRow.add(buttonFirst);
                    keyboardFirstRow.add(buttonSecond);
                    keyboardFirstRow.add(buttonThree);
                }
                if(msg.equals("Мои записи")){
                    buttonFirst.setText("Да").setCallbackData("Да_М");
                    buttonSecond.setText("Нет").setCallbackData("Нет_М");
                    buttonThree.setText("Назад").setCallbackData("Назад_Уд");

                    keyboardFirstRow.add(buttonFirst);
                    keyboardFirstRow.add(buttonSecond);
                    keyboardFirstRow.add(buttonThree);
                }
            }
        }

        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(keyboardFirstRow);
        return rowList;
    }
}
