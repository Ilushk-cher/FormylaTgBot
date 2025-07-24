package akilsuh.proj.service.commands;

import akilsuh.proj.Message;
import akilsuh.proj.MessageUtil;
import akilsuh.proj.entity.AppUser;
import akilsuh.proj.enums.UserRole;
import akilsuh.proj.service.enums.states.RegistrationStates;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static akilsuh.proj.enums.UserState.BASIC_STATE;
import static akilsuh.proj.service.enums.states.RegistrationStates.ASKING_ROLE;

public class RegistrationCommand {
    private final String STUDENT = "STUDENT_BUTTON";
    private final String PARENT = "PARENT_BUTTON";
    private final String BACK = "BACK_BUTTON";
    private final String CONFIRM = "CONFIRM_BUTTON";
    private static final String regex = "&";
    private final HashMap<Long, RegistrationStates> states = new HashMap<>();
    private final HashMap<Long, AppUser> users = new HashMap<>();
    public Message addUser(AppUser appUser) {
        states.put(appUser.getTelegramUserId(), ASKING_ROLE);
        users.put(appUser.getTelegramUserId(), appUser);
        return generateMessage(0);
    }

    public String userToString(AppUser appUser) {
        Long id = appUser.getTelegramUserId();
        AppUser newAppUser = users.get(id);
        if (newAppUser == null) {
            return "Ошибка, пользователь не найден";
        }
        return newAppUser.toString();
    }

    private Message generateMessage(int index) {
        Message message;
        int callbackId = (int) (Math.random() * Integer.MAX_VALUE);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineBack = new ArrayList<>();

        var backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData(BACK + regex + callbackId);

        rowInLineBack.add(backButton);
        if (index == 0) {
            var studentButton = new InlineKeyboardButton();
            studentButton.setText("Ученик");
            studentButton.setCallbackData(STUDENT + regex + callbackId);

            var parentButton = new InlineKeyboardButton();
            parentButton.setText("Родитель");
            parentButton.setCallbackData(PARENT + regex + callbackId);

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            rowInLine.add(studentButton);
            rowInLine.add(parentButton);
            rowsInLine.add(rowInLine);
            rowsInLine.add(rowInLineBack);

            inlineKeyboardMarkup.setKeyboard(rowsInLine);

            message = Message.builder()
                    .text("Выберите вашу роль")
                    .inlineKeyboardMarkup(inlineKeyboardMarkup)
                    .build();
        } else if (index == 6) {
            var confirmButton = new InlineKeyboardButton();
            confirmButton.setText("Подтвердить");
            confirmButton.setCallbackData(CONFIRM + regex + callbackId);

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            rowInLine.add(confirmButton);
            rowsInLine.add(rowInLine);
            rowsInLine.add(rowInLineBack);

            inlineKeyboardMarkup.setKeyboard(rowsInLine);

            message = Message.builder()
                    .text("confirm")
                    .inlineKeyboardMarkup(inlineKeyboardMarkup)
                    .build();
        } else {
            rowsInLine.add(rowInLineBack);
            inlineKeyboardMarkup.setKeyboard(rowsInLine);
            switch (index) {
                case 1: message = Message.builder().text("Введите ваше имя").build(); break;
                case 2: message = Message.builder().text("Введите вашу фамилию").build(); break;
                case 3: message = Message.builder().text("Введите класс обучения\n(целое число от 1го до 11ти)").build(); break;
                case 4: message = Message.builder().text("Введите номер группы\n(целое число от 1го до 3х)").build(); break;
                case 5: message = Message.builder().text("Введите ваш номер телефона").build(); break;
                default:
                    message = Message.builder()
                            .text("Произошла ошибка, попробуйте ввести /cancel и выполните нужную команду заново")
                            .callBackId(String.valueOf(callbackId)).build();
                    return message;
            }
            message.setInlineKeyboardMarkup(inlineKeyboardMarkup);
        }
        message.setCallBackId(String.valueOf(callbackId));
        return message;
    }

    public boolean hasUser(AppUser appUser) {
        return states.get(appUser.getTelegramUserId()) != null && users.get(appUser.getTelegramUserId()) != null;
    }

    public boolean hasUser(Long id) {
        return states.get(id) != null && users.get(id) != null;
    }

    public AppUser registerUser(AppUser appUser) {
        states.remove(appUser.getTelegramUserId());
        AppUser user = users.get(appUser.getTelegramUserId());
        users.remove(appUser.getTelegramUserId());
        user.setState(BASIC_STATE);
        user.setIsActive(true);
        return user;
    }

    public Message processTextMessage(AppUser appUser, String text) {
        Long id = appUser.getTelegramUserId();
        Message message;
        if (!hasUser(id)) {
            return generateMessage(-1);
        }
        RegistrationStates state = states.get(id);
        AppUser user = users.get(id);
        switch (state) {
            case ASKING_ROLE:
                message = generateMessage(0);
                message.setText("Ожидается нажание на кнопку\n\n" + message.getText());
                return message;
            case ASKING_FIRST_NAME:
                user.setFirstName(prepareText(text));
                break;
            case ASKING_LAST_NAME:
                user.setLastName(prepareText(text));
                break;
            case ASKING_CLASS:
                try {
                    int classNum = Integer.parseInt(text.trim());
                    if (classNum < 1 || classNum > 11) {
                        message = generateMessage(3);
                        message.setText("Такого класса не существует, класс может быть только целым числом от 1го до 11ти\n\n" + message.getText());
                        return message;
                    }
                    user.setClassNum(classNum);
                    break;
                } catch (NumberFormatException e) {
                    message = generateMessage(3);
                    message.setText("Класс может быть только целым числом от 1го до 11ти\n\n" + message.getText());
                    return message;
                }
            case ASKING_GROUP:
                try {
                    int groupNum = Integer.parseInt(text.trim());
                    if (groupNum < 1 || groupNum > 3) {
                        message = generateMessage(3);
                        message.setText("Такой группы не существует, группа может быть только целым числом от 1го до 3х\n\n" + message.getText());
                        return message;
                    }
                    user.setGroupNum(groupNum);
                    break;
                } catch (NumberFormatException e) {
                    message = generateMessage(4);
                    message.setText("Группа может быть только целым числом от 1го до 3х\n\n" + message.getText());
                }
            case ASKING_PHONE_NUMBER:
                //TODO добавить валидатор номера телефона
                user.setPhoneNumber(text);
                break;
            case CONFIRM:
                message = generateMessage(6);
                return message;
            default:
                return generateMessage(-1);
        }
        users.replace(id, user);
        int newStateOrd = state.ordinal() + 1;
        states.replace(id, RegistrationStates.values()[newStateOrd]);
        message = generateMessage(newStateOrd);
        return message;
    }

    public Message processCallbackMessage(AppUser appUser, CallbackQuery callbackQuery) {
        Long id = appUser.getTelegramUserId();
        String[] callbackArgs = callbackQuery.getData().split(regex);
        Message message;
        if (!hasUser(id) || callbackArgs.length != 2) {
            return generateMessage(-1);
        }
        String buttonName = callbackArgs[0];
        String updateCallbackId = callbackArgs[1];
        String userCallBackId = appUser.getCallBackId();
        if (!updateCallbackId.equals(userCallBackId)) {
            return Message.builder().text("null").build();
        }
        RegistrationStates state = states.get(id);
        AppUser mapUser = users.get(id);



        if (BACK.equals(buttonName)) {
            if (state.ordinal() > 0) {
                int newStateOrd = state.ordinal() - 1;
                states.replace(id, RegistrationStates.values()[newStateOrd]);
                message = generateMessage(newStateOrd);
            } else {
                message = Message.builder().text("cancel").build();

            }
        } else if (CONFIRM.equals(buttonName)) {
            message = Message.builder().text("complete").build();
        } else {
            if (STUDENT.equals(buttonName)) {
                mapUser.setRole(UserRole.STUDENT);
            } else if (PARENT.equals(buttonName)) {
                mapUser.setRole(UserRole.PARENT);
            }
            message = generateMessage(1);
            users.replace(id, mapUser);
            states.replace(id, RegistrationStates.values()[state.ordinal() + 1]);
        }
        message.setMessageId(MessageUtil.getMessageId(callbackQuery));
        message.setEdit(true);
        return message;
    }

    public String prepareText(String text) {
        String readyText = text.trim();
        return readyText.substring(0, 1).toUpperCase() + readyText.substring(1).toLowerCase();
    }
}
