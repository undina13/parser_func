package com.undina.parser_func.service;

import com.undina.parser_func.exception.FunctionException;
import lombok.experimental.UtilityClass;

import java.util.HashMap;

@UtilityClass
public class FunctionMap {


    public HashMap<String, Function> getFunctionMap() {
        HashMap<String, Function> functionTable = new HashMap<>();
        functionTable.put("min", args -> {
            if (args.isEmpty()) {
                throw new FunctionException("No arguments for function min");
            }
            double min = args.get(0);
            for (Double val : args) {
                if (val < min) {
                    min = val;
                }
            }
            return min;
        });
        functionTable.put("pow", args -> {
            if (args.size() != 2) {
                throw new FunctionException("Wrong argument count for function pow: " + args.size());
            }
            return Math.pow(Math.round(args.get(0)), Math.round(args.get(1)));
        });
        functionTable.put("rand", args -> {
            if (!args.isEmpty()) {
                throw new FunctionException("Wrong argument count for function rand");
            }
            return (Math.random() * 256f);
        });
        functionTable.put("avg", args -> {
            double sum = 0;
            for (int i = 0; i < args.size(); i++) {
                sum += args.get(i);
            }
            return sum / args.size();
        });
        functionTable.put("abs", args -> {
            if (args.size() != 1) {
                throw new FunctionException("Wrong argument count for function abs");
            }
            return Math.abs(args.get(0));
        });
        return functionTable;
    }
}
