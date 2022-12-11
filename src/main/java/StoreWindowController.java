import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class StoreWindowController implements WindowListener {

    private final StoreModel model;

    public StoreWindowController(StoreModel model) {
        this.model = model;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        model.quit();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
