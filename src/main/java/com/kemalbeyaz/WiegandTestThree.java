package com.kemalbeyaz;

import com.pi4j.io.gpio.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class WiegandTestThree{

    public static char[] s = new char[26];
    static int bits = 0;
    static long startTime = 0;
    static boolean enabledCard = false;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("pi4j.linking", "dynamic");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin 0 and 1 as an input pin with its internal pull down
        // resistor enabled
        final GpioPinDigitalInput pin0 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP);
        final GpioPinDigitalInput pin1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_UP);

        final GpioPinDigitalOutput pin2red = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "MyRedLED", PinState.LOW);
        final GpioPinDigitalOutput pin3green = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "MyGreenLED", PinState.LOW);
        final GpioPinDigitalOutput pin4Blue = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "MyBlueLED", PinState.LOW);
        System.out.println("PINs ready");

        Thread th = new Thread(new Runnable() {
            public void run() {
                while (true) {

                    if (pin0.isLow()) { // D0 on ground?
                        s[bits++] = '0';
                        while (pin0.isLow()) { }

                        if(bits == 1)
                            startTime = System.currentTimeMillis();
                    }

                    if (pin1.isLow()) { // D1 on ground?
                        s[bits++] = '1';
                        while (pin1.isLow()) { }

                        if(bits == 1)
                            startTime = System.currentTimeMillis();
                    }

                    if (bits == 26) {
                        bits=0;
                        startTime = 0;
                        Print();

                        if(enabledCard)
                            pin3green.pulse(500,true);
                        else
                            pin2red.pulse(650,true);
                        enabledCard = false;
                    }

                    if(startTime != 0 && (System.currentTimeMillis()-startTime)>100){
                        bits=0;
                        startTime = 0;
                        System.out.println("Hooop!");
                        pin4Blue.pulse(200,true);
                    }
                }
            } // end of run
        }); // end of thread

        th.setPriority(Thread.MAX_PRIORITY);
        th.start();
        System.out.println("Thread start");

        // keep program running until user aborts (CTRL-C)
        while(true) {
            Thread.sleep(500);
        }
    }

    protected static void Print() {

        String sonuc = "";
        for (int i = 0; i < 26; i++) {
            sonuc = sonuc+s[i];
        }

        int decimal = Integer.parseInt(sonuc,2);
        String hexStr = Integer.toString(decimal,16);
        System.out.println("Binary: " +sonuc);
        System.out.println("Hex: "+hexStr);
        System.out.println("Decimal: "+hex2decimal(hexStr));

        String facilityString = sonuc.substring(1,9);
        int facilityDecimal = Integer.parseInt(facilityString,2);
        String hexStrFacility = Integer.toString(facilityDecimal,16);
        System.out.println("Facility Code: " + hexStrFacility);
        System.out.println("Facility Code Decimal: " +hex2decimal(hexStrFacility));

        String cardNumber = sonuc.substring(9,25);
        int cardNumberDecimal = Integer.parseInt(cardNumber,2);
        String hexStringCardNumber = Integer.toString(cardNumberDecimal,16);
        System.out.println("Card Number: " +hexStringCardNumber);
        System.out.println("Card Number Decimal: " + hex2decimal(hexStringCardNumber));

        enabledCard = CardControl.isEnabledCard(hex2decimal(hexStringCardNumber));
        if(enabledCard)
            System.out.println("Bu kart yetkili...");
        else
            System.out.println("Bu kart yetkili değil...");
    }

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }
}
