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

        for (int i = 5; i >= -3; i--) {

            if (i > 0) {
                if (running) {
                    System.out.println(); //ez a sor oldja meg azt a problémát, hogy a beírt tippelt betü után ne rakjon be számot a visszaszámlálás
                    System.out.print(i + "!!! ");
                } else {
                    i = 6;
                }
            } else {
                if (running) {
                    System.out.println(); //ez a sor oldja meg azt a problémát, hogy a beírt tippelt betü után ne rakjon be számot a visszaszámlálás
                    System.out.println("Free to rob!!! ");
                    running = false;
                    robberyWasSoon = true;
                    i = 6;
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Exception in Robbery Class run() " + e);
            }
        }

    }
}
