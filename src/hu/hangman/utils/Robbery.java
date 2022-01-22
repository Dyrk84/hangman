package hu.hangman.utils;

public class Robbery extends Thread {

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private volatile boolean running = true;

    public boolean isRobberyWasSoon() {
        return robberyWasSoon;
    }

    public void setRobberyWasSoon(boolean robberyWasSoon) {
        this.robberyWasSoon = robberyWasSoon;
    }

    private volatile boolean robberyWasSoon = false;


    @Override
    public void run() {

        for (int i = 5; i >= 0; i--) {

            if (i > 0) {
                if (running) { //akkor van bekapcsolva, amikor elindul egy játék kör. Ha nincs bekapcsolva, nem ír ki semmit.
                    System.out.println(); //ez a sor oldja meg azt a problémát, hogy a beírt tippelt betü után ne rakjon be számot a visszaszámlálás
                    System.out.print(i + "!!! ");
                } else {
                    i = 6; //ha nincs bekapcsolva a running, akkor mindig ide lép, és beállítja az i-t 6-ra.
                }
            } else { //ide csak akkor lép be, ha megtörténik a visszaszámlálás
                if (running) {
                    System.out.println(); //ez a sor oldja meg azt a problémát, hogy a beírt tippelt betü után ne rakjon be számot a visszaszámlálás
                    System.out.println("Free to rob!!! ");
                    running = false;
                    robberyWasSoon = true;
                    i = 6; //ezzel elérem, hogy az i miatt az első 29. sorban lévő if fusson le, ami aztán azt eredményezi, hogy az i mindig 6 lesz, amíg el nem kezdődik az új visszaszámlálás
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Exception in hu.hangman.utils.Robbery Class run() " + e);
            }
        }

    }
}
