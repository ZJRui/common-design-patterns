package com.sachin.debezium.util;

import java.util.Optional;

/**
 * @Author Sachin
 * @Date 2021/6/18
 **/
public class HelloService {

    public static void test(){

    }

    public static void main(String[] args) {



    }
    public void testA(){
        VariableLatch variableLatch = null;
        Optional<VariableLatch> latchOptional = Optional.ofNullable(variableLatch);
        latchOptional.ifPresent(TestHello::testB);
    }
}
