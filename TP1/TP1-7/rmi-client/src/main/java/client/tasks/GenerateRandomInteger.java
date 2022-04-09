package client.tasks;

import shared.Task;

import java.io.Serializable;
import java.util.Random;

public class GenerateRandomInteger implements Task<Integer>, Serializable {

    private static final long serialVersionUID = 227L;

    @Override
    public Integer execute() {
        return new Random().ints(0, 500)
                .findFirst()
                .getAsInt();
    }
}
