package uk.ac.cam.cl.git;

public class HereIsYourException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public HereIsYourException() {
        super("Well you did ask for it...");
    }

}
