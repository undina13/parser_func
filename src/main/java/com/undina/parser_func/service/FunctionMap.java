package com.undina.parser_func.service;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
@UtilityClass
public class FunctionMap {
   // private HashMap<String, Function> functionTable ;

    public HashMap<String, Function> getFunctionMap() {
        HashMap<String, Function> functionTable = new HashMap<>();
        functionTable.put("min", args -> {
            if (args.isEmpty()) {
                throw new RuntimeException("No arguments for function min");
            }
            int min = args.get(0);
            for (Integer val: args) {
                if (val < min) {
                    min = val;
                }
            }
            return min;
        });
        functionTable.put("pow", args -> {
            if (args.size() != 2) {
                throw new RuntimeException("Wrong argument count for function pow: " + args.size());
            }
            return (int) Math.pow(args.get(0), args.get(1));
        });
        functionTable.put("rand", args -> {
            if (!args.isEmpty()) {
                throw new RuntimeException("Wrong argument count for function rand");
            }
            return (int)(Math.random() * 256f);
        });
        functionTable.put("avg", args -> {
            int sum = 0;
            for (int i = 0; i < args.size(); i++) {
                sum += args.get(i);
            }
            return sum / args.size();
        });
        return  functionTable;
    }
}
