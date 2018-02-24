package com.kemalbeyaz;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class WiegandTestThree {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("pi4j.linking", "dynamic");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin 0 and 1 as an input pin with its internal pull down
        // resistor enabled
        final GpioPinDigitalInput pin0 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP);
        final GpioPinDigitalInput pin1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_UP);

        System.out.println("PINs ready");

        pin0.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                System.out.println("0 --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
            }

        });

        pin1.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                System.out.println("1 --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
            }

        });

        // keep program running until user aborts (CTRL-C)
        while(true) {
            Thread.sleep(500);
        }

    }
}
