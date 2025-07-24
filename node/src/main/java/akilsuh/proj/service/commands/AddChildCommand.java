package akilsuh.proj.service.commands;

import akilsuh.proj.Message;
import akilsuh.proj.MessageUtil;
import akilsuh.proj.entity.AppUser;
import akilsuh.proj.service.commands.usersWithStates.AddChildUser;
import akilsuh.proj.service.enums.states.AddChildStates;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddChildCommand {
    private final String NAME = "NAME_BUTTON";
    private final String CLASS_NUM = "CLASS_NUM_BUTTON";
    private final String BACK = "BACK_BUTTON";
    private static final String regex = "&";
    private final HashMap<Long, AddChildUser> mapOfUsers = new HashMap<>();

    public Message addUser(AppUser appUser, List<AppUser> students) {
        mapOfUsers.put(appUser.getTelegramUserId(), AddChildUser.builder()
                .user(appUser)
                .state(AddChildStates.ASKING_LASTNAME)
                .students(students)
                .build());
        return generateMessage(0, appUser.getTelegramUserId());
    }

    public Message processTextMessage(AppUser appUser, String text) {
        Long id = appUser.getTelegramUserId();
        Message message;
        if (!hasUser(id)) {
            return generateMessage(-1, id);
        }
        AddChildUser addChildUser = mapOfUsers.get(id);
        AddChildStates state = addChildUser.getState();

        switch (state) {
            case ASKING_LASTNAME:
                addChildUser.setLastName(text);
                message = generateMessage(1, id);
                break;
            case ASKING_CLASS:
                message = generateMessage(-1, id);
                break;
            default:
                message = Message.builder().text("пшел нах").build();
        }
        return message;
    }

    public Message processCallbackMessage(AppUser appUser, CallbackQuery callbackQuery) {
        Long id = appUser.getTelegramUserId();
        String[] callbackArgs = callbackQuery.getData().split(regex);
        Message message;
        if (!hasUser(id) || callbackArgs.length != 2) {
            return generateMessage(-1, appUser.getTelegramUserId());
        }
        String buttonName = callbackArgs[0];
        String updateCallbackId = callbackArgs[1];
        String userCallBackId = appUser.getCallBackId();
        if (!updateCallbackId.equals(userCallBackId)) {
            return Message.builder().text("null").build();
        }
        AddChildUser addChildUser = mapOfUsers.get(id);
        AddChildStates state = addChildUser.getState();

        if (BACK.equals(buttonName)) {
            if (state.ordinal() > 0) {
                int newStateOrd = state.ordinal() - 1;
                addChildUser.setState(AddChildStates.values()[newStateOrd]);
                mapOfUsers.replace(id, addChildUser);
                message = generateMessage(newStateOrd, id);
            } else {
                message = Message.builder().text("cancel").build();
            }
        } else {
            message = generateMessage(-1, id);
        }
        message.setMessageId(MessageUtil.getMessageId(callbackQuery));
        message.setEdit(true);
        return message;
    }

    public Message generateMessage(int index, Long id) {
        Message message = Message.builder().build();
        AddChildUser addChildUser = mapOfUsers.get(id);
        int callbackId = (int) (Math.random() * Integer.MAX_VALUE);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineBack = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData(BACK + regex + callbackId);

        rowInLineBack.add(backButton);

        if (index == 0) {
            message = Message.builder().text("Введите фамилию ребенка").build();
            rowsInLine.add(rowInLineBack);
        } else if (index == 1) {
            List<Integer> uniqueClassNum = addChildUser.getUniqueClassNum();
            for (Integer classNum : uniqueClassNum) {
                rowInLine.add(generateClassNumButton(classNum, callbackId));
            }
            rowsInLine.add(rowInLine);
            rowsInLine.add(rowInLineBack);
            message = Message.builder().text("По введенной фамилии " + addChildUser.getLastName() + " найдены совпадения в следующих классах.\nВыберите нужный, нажав на соответствующую кнопку").build();
        } else {
            message.setText("ауе");
        }

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        message.setInlineKeyboardMarkup(inlineKeyboardMarkup);
        message.setCallBackId(String.valueOf(callbackId));
        return message;
    }

    private InlineKeyboardButton generateClassNumButton(int classNum, int callbackId) {
        var classNumButton = new InlineKeyboardButton();
        classNumButton.setText(String.valueOf(classNum));
        classNumButton.setCallbackData(classNum + regex + callbackId);
        return classNumButton;
    }

    public boolean hasUser(Long id) {
        return mapOfUsers.get(id) != null;
    }
}