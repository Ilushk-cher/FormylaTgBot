package akilsuh.proj.service.impl;

import akilsuh.proj.Message;
import akilsuh.proj.dao.AppUserDAO;
import akilsuh.proj.dao.RawDataDAO;
import akilsuh.proj.entity.AppUser;
import akilsuh.proj.entity.RawData;
import akilsuh.proj.enums.UserRole;
import akilsuh.proj.service.MainService;
import akilsuh.proj.service.ProducerService;
import akilsuh.proj.service.commands.AddChildCommand;
import akilsuh.proj.service.commands.RegistrationCommand;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static akilsuh.proj.enums.UserState.*;
import static akilsuh.proj.service.enums.commands.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final RegistrationCommand registrationCommand;
    private final AddChildCommand addChildCommand;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, RegistrationCommand registrationCommand, AddChildCommand addChildCommand) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.registrationCommand = registrationCommand;
        this.addChildCommand = addChildCommand;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var sendMessage = Message.builder().build();

        if (CANCEL.equals(text)) {
            appUser.setState(BASIC_STATE);
            sendMessage.setText("cancel done");
        } else if (BASIC_STATE.equals(userState)) {
            sendMessage = processServiceCommand(appUser, text);
        } else if (REGISTRATION_STATE.equals(userState)) {
            sendMessage = registrationCommand.processTextMessage(appUser, text);
            if (sendMessage.getText().equals("confirm")) {
                sendMessage.setText("Проверьте данные и подтвердите регистрацию\n\n" + registrationCommand.userToString(appUser));
            }
        } else if (ADD_CHILD_STATE.equals(userState)) {
            sendMessage = addChildCommand.processTextMessage(appUser, text);
        }

        appUser.setCallBackId(sendMessage.getCallBackId());
        appUserDAO.save(appUser);
        //TODO реализация обработки команд
//        if (CANCEL.equals(text)) {
//            output = "cancel";
//            appUser.setState(BASIC_STATE);
//            appUserDAO.save(appUser);
//        } else if (BASIC_STATE.equals(userState)) {
//            output = processServiceCommand(appUser, text);
//        } else if (UserState.WAIT_FOR_EMAIL_STATE.equals(userState)) {
//            //TODO хз надо ли это реализовывать
//        } else if (UserState.REGISTRATION_STATE.equals(userState)) {
//            output = RegistrationCommand.processTextMessage(appUser.getTelegramUserId(), text);
//        } else {
//            log.error("Unknown user state: " + userState);
//            output = "произошла хуйня сорян";
//        }
        var chatId = update.getMessage().getChatId();
        sendAnswer(sendMessage, chatId);
    }

    private String processActiveUserCommand() {
        //TODO
        return "j";
    }

    @Override
    public void processCallbackMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        Message sendMessage = Message.builder().text("null").build();

        if (REGISTRATION_STATE.equals(userState)) {
            sendMessage = registrationCommand.processCallbackMessage(appUser, update.getCallbackQuery());
            if (sendMessage.getText().equals("cancel")) {
                appUser.setState(BASIC_STATE);
                sendMessage.setText("Регистрация отменена");
            } else if (sendMessage.getText().equals("complete")) {
                appUser = registrationCommand.registerUser(appUser);
                sendMessage.setText("Регистрация завершена, ваш профиль активирован");
            }
        } else if (ADD_CHILD_STATE.equals(userState)) {
            sendMessage = addChildCommand.processCallbackMessage(appUser, update.getCallbackQuery());
            if (sendMessage.getText().equals("cancel")) {
                appUser.setState(BASIC_STATE);
                sendMessage.setText("Отмена");
            }
        }
        if (sendMessage.getText().equals("null")) {
            return;
        }

        appUser.setCallBackId(sendMessage.getCallBackId());
        appUserDAO.save(appUser);

        var chatId = update.getCallbackQuery().getMessage().getChatId();
        sendAnswer(sendMessage, chatId);
    }

    private void sendAnswer(Message message, Long chatId) {
        message.setChatId(chatId);
        producerService.produceAnswer(message);
    }

    private Message processServiceCommand(AppUser appUser, String cmd) {
        Message message = Message.builder().build();
        if (REGISTRATION.equals(cmd)) {
            //TODO пиздец бля
            appUser.setState(REGISTRATION_STATE);
            appUserDAO.save(appUser);
            message = registrationCommand.addUser(appUser);
//            return "я ебал эту регистрацию, потом сделаю";
        } else if (HELP.equals(cmd)) {
            message.setText("мне бы кто помог");
        } else if (START.equals(cmd)) {
            message.setText("Добро пожаловать!\nДля регистрации аккаунта введите или выберите команду /registration");
        } else if ("/my_account".equals(cmd)) {
            message.setText("Ваш аккаунт\n\n" + appUser.toString());
        } else if ("add_child".equals(cmd)) {
            appUser.setState(ADD_CHILD_STATE);
            appUserDAO.save(appUser);
            message = addChildCommand.addUser(appUser, appUserDAO.findAppUserByRole(UserRole.STUDENT));
        } else {
            message.setText("сам понял что сказал?");
        }
        return message;
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser;
        if (update.hasCallbackQuery()) {
            telegramUser = update.getCallbackQuery().getFrom();
        } else {
            telegramUser = update.getMessage().getFrom();
        }
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .tgFirstName(telegramUser.getFirstName())
                    .tgLastName(telegramUser.getLastName())
                    .username(telegramUser.getUserName())
                    //TODO придумать проверку
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
