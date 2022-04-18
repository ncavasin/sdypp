package com.sdypp.code;

import com.sdypp.code.shared.Task;

import java.io.Serializable;
import java.util.Random;

public class GenerateRandomInteger implements Task<Integer>, Serializable {

    private static final long serialVersionUID = 227;

    @Override
    public Integer execute() {
        return new Random().ints(0, 500)
                .findFirst()
                .getAsInt();
    }
}
