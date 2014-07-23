package uk.ac.cam.cl.git.api;

public class HereIsYourException extends Exception {

    private static final long serialVersionUID = 2L;

    public HereIsYourException() {
        super("Well you did ask for it...");
    }

}
