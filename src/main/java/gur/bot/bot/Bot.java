package gur.bot.bot;

import gur.bot.domain.model.SomeEntity;
import gur.bot.repository.EntityRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    private final EntityRepository entityRepository;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String str = update.getMessage().getText();
            switch (str) {
                case ("Hello"):
                    try {
                        execute(sendInlineKeyBoardMessage(update.getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    return;
                case ("/help"):
                    try {
                        SendMessage sendMessage = new SendMessage();
                        execute(sendMessage.setChatId(update.getMessage().getChatId())
                                .setText("Type text to reverse string."));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    return;
                default: {
                    try {
                        SendMessage sendMessage = new SendMessage();
                        entityRepository.save(SomeEntity.builder()
                                .name(update.getMessage().getText())
                                .description("Description: " + update.getMessage().getFrom())
                                .number(new Random().nextInt(100))
                                .build());
                        execute(sendMessage.setChatId(update.getMessage().getChatId())
                                .setText(new StringBuilder(str).reverse().toString()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            try {
                execute(new SendMessage().setText(
                        update.getCallbackQuery().getData())
                        .setChatId(update.getCallbackQuery().getMessage().getChatId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    // Геттеры, которые необходимы для наследования от TelegramLongPollingBot
    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }

    public static SendMessage sendInlineKeyBoardMessage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Button 1");
        inlineKeyboardButton1.setCallbackData("Button \"1\" has been pressed");
        inlineKeyboardButton2.setText("Button 2");
        inlineKeyboardButton2.setCallbackData("Button \"2\" has been pressed");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Button with callback").setCallbackData("Callback"));
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(chatId).setText("Пример").setReplyMarkup(inlineKeyboardMarkup);
    }
}
