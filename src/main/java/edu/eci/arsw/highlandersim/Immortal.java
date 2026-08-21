package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    public static boolean paused = false;
    public static boolean stopped = false;
    public static final Object pauseLock = new Object();
    public static final java.util.concurrent.atomic.AtomicInteger pausedCount = new java.util.concurrent.atomic.AtomicInteger(0);


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {

        while (!stopped && this.health > 0) {
            if (paused) {
                synchronized (pauseLock) {
                    pausedCount.incrementAndGet();
                    if (pausedCount.get() >= immortalsPopulation.size()) {
                        pauseLock.notifyAll(); 
                    }
                    while (paused) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    pausedCount.decrementAndGet();
                }
            }

            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);

            this.fight(im);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        immortalsPopulation.remove(this);
    }

    public void fight(Immortal i2) {
        Immortal firstLock = this;
        Immortal secondLock = i2;
        
        if (System.identityHashCode(this) > System.identityHashCode(i2)) {
            firstLock = i2;
            secondLock = this;
        }

        synchronized (firstLock) {
            synchronized (secondLock) {


                if (i2.getHealth() > 0 && this.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
                    
                    if (i2.getHealth() <= 0) {
                        immortalsPopulation.remove(i2);
                    }
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }

    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

}
