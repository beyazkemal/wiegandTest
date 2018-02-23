package com.kemalbeyaz;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

import java.util.Date;
import java.util.Timer;

public class WiegandTestTwo {

        public static char[] s = new char[100];
        static int bits = 0;

        public static void main(String[] args) {
            System.setProperty("pi4j.linking", "dynamic");

            // create gpio controller
            final GpioController gpio = GpioFactory.getInstance();

            // provision gpio pin #02 as an input pin with its internal pull down
            // resistor enabled
            final GpioPinDigitalInput pin0 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP);
            final GpioPinDigitalInput pin1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_UP);

            System.out.println("PINs ready");
            Thread th = new Thread(new Runnable() {
                public void run() {

                    while (true) {

                        if (pin0.isLow()) { // D0 on ground?
                            s[bits++] = '0';
                            while (pin0.isLow()) { }
                            System.out.println(0);
                        }

                        if (pin1.isLow()) { // D1 on ground?
                            s[bits++] = '1';
                            while (pin1.isLow()) { }
                            System.out.println(1);
                        }

                        if (bits == 50) {
                            bits=0;
                            Print();

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            });
            th.setPriority(Thread.MAX_PRIORITY);
            th.start();
            System.out.println("Thread start");

            for (;;) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        protected static void Print() {
            for (int i = 0; i < 55; i++) {
                System.out.write(s[i]);
            }
            System.out.println();
            bits = 0;
        }
}