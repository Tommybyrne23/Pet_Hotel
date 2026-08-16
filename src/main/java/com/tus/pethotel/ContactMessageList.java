package com.tus.pethotel;

import java.util.ArrayList;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class ContactMessageList {

    private final List<ContactMessage> messages = new ArrayList<>();

    public boolean addMessage(ContactMessage message) {
        if (message == null
                || isBlank(message.getName())
                || isBlank(message.getEmail())
                || isBlank(message.getSubject())
                || isBlank(message.getMessage())) {
            return false;
        }
        messages.add(message);
        return true;
    }

    public List<ContactMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public int getNumberOfMessages() {
        return messages.size();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}