package ru.korshun;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.korshun.core.license.License;
import ru.korshun.core.license.LicenseManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    public synchronized void testButtons(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Текстовые кнопки");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> line = new ArrayList<>();
        line.add(InlineKeyboardButton.builder().text("коко").callbackData("coco").build());
        line.add(InlineKeyboardButton.builder().text("Другой Коко").callbackData("other_coco").build());
        rows.add(line);
        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);
        executeMethod(message);
    }

    private void setupMenu(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Бот готов к использованию!");
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("\uD83C\uDF81 Купить");
        rows.add(row);
        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);
        markup.setResizeKeyboard(true);
        executeMethod(message);
    }

    public synchronized SendMessage sendMessage(String chatId, String msg) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(msg);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return sendMessage;
    }
    public void sendCategoriesMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите категорию:");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> line = new ArrayList<>();
        line.add(InlineKeyboardButton.builder().text("Майнкрафт").callbackData("minecraft").build());
        rows.add(line);
        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);
        executeMethod(message);
    }
    public void sendPayment(String chatId, String currency, String title, String description, String payload, int price) {
        SendInvoice invoice = new SendInvoice();
        invoice.setChatId(String.valueOf(chatId));
        invoice.setTitle(title);
        invoice.setDescription(description);
        invoice.setPayload(payload);
        invoice.setProviderToken(Main.getProperties().getProperty("payments.token"));
        invoice.setCurrency(currency);
        invoice.setNeedEmail(true);
        List<LabeledPrice> prices = new ArrayList<>();
        prices.add(new LabeledPrice("Лицензия майнкрафт", price * 100));
        invoice.setPrices(prices);
        executeMethod(invoice);
    }
    public synchronized SendMessage sendMessage(long chatId, String msg) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(msg);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return sendMessage;
    }

    private void sendBuyLicense(String chatId, int messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText("\uD83C\uDF81 Ключ лицензии майнкрафт\nЦена: 1500₽\n ");
        message.setMessageId(messageId);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> line = new ArrayList<>();
        line.add(InlineKeyboardButton.builder().text("\uD83C\uDF81 Купить").callbackData("buyLicense").build());
        line.add(InlineKeyboardButton.builder().text("❌ Отменить").callbackData("cancelPayment").build());
        rows.add(line);
        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);
        executeMethod(message);
    }

    @Override
    public String getBotUsername() {
        return "MinecraftLicenseBot";
    }

    @Override
    public String getBotToken() {
        return Main.getProperties().getProperty("token");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();
            if(text.equals("/start")) {
                setupMenu(chatId);
            }
            else if(text.equals("\uD83C\uDF81 Купить")) {
                sendCategoriesMessage(Long.parseLong(chatId));
            }
        }
        else if(update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if(callbackData.equals("minecraft")) {
                EditMessageText messageText = new EditMessageText();
                messageText.setChatId(String.valueOf(chatId));
                messageText.setText("\uD83C\uDF81 Ключ лицензии майнкрафт\nЦена: 1500₽\n ");
                messageText.setMessageId((int) messageId);
                executeMethod(messageText);
                sendBuyLicense(String.valueOf(chatId), (int) messageId);
            }
            else if(callbackData.equals("buyLicense")) {
                sendPhoto("images/license.jpg", String.valueOf(chatId), "\uD83C\uDF81 Ключ лицензии майнкрафт\nЦена: 1500₽\nНаличие: " + LicenseManager.getLicenses().size());
                sendPayment(String.valueOf(chatId), "RUB", "Лицензия майнкрафт", "Оплата", "ds", 1500);
            }
            else if(callbackData.equals("cancelPayment")) {
                EditMessageText messageText = new EditMessageText();
                messageText.setChatId(String.valueOf(chatId));
                messageText.setText("\uD83C\uDF81 Ключ лицензии майнкрафт\nЦена: 1500₽\n ");
                messageText.setMessageId((int) messageId);
                executeMethod(messageText);
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(String.valueOf(chatId));
                deleteMessage.setMessageId((int) messageId);
                executeMethod(deleteMessage);
                sendCategoriesMessage(chatId);
            }
        }
        if(update.hasPreCheckoutQuery()) {
            PreCheckoutQuery preCheckoutQuery = update.getPreCheckoutQuery();
            executeMethod(AnswerPreCheckoutQuery.builder().preCheckoutQueryId(preCheckoutQuery.getId()).ok(true).errorMessage("Спасибо за оплату!").build());
        }
        if(update.hasMessage() && update.getMessage().hasSuccessfulPayment()) {
            SuccessfulPayment successfulPayment = update.getMessage().getSuccessfulPayment();
            License license = LicenseManager.random();
            LicenseManager.remove(license);
            sendMessage(update.getMessage().getChatId(), "Спасибо за оплату покупки!\nВаш лицензионный ключ: " + license.getKey() + "\nИнструкция по активации: dh.com");
            LicenseManager.getLicenses().forEach(license1 -> System.out.println(license1.getKey()));
        }
    }

    public void sendPhoto(String url, String chatId, String caption) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(new File("src/main/resources/" + url)));
        sendPhoto.setCaption(caption);
        executeMethod(sendPhoto);
    }

    public void executeMethod(BotApiMethod<?> method) {
        try {
            execute(method);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeMethod(SendPhoto sendPhoto) {
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
