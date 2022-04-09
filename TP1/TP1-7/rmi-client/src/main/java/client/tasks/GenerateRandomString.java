package client.tasks;

import org.apache.commons.lang.RandomStringUtils;
import shared.Task;

import java.io.Serializable;

public class GenerateRandomString implements Task<String>, Serializable {
    @Override
    public String execute() {
        int length = 10;
        boolean useLetters = true;
        boolean userNumbers = true;
        return RandomStringUtils.random(length, useLetters, userNumbers);
    }
}
