package ru.korshun;


import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.objects.UserProfilePhotos;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.korshun.core.license.License;
import ru.korshun.core.license.LicenseManager;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {
            LicenseManager.add(new License("key1"));
            LicenseManager.add(new License("key2"));
            LicenseManager.add(new License("key3"));
            LicenseManager.add(new License("key4"));
            LicenseManager.add(new License("key5"));
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new Bot());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("src/main/resources/bot.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}