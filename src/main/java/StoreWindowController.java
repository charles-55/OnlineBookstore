import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class StoreWindowController implements WindowListener {

    private final StoreModel model;
    private final StoreFrame frame;

    public StoreWindowController(StoreModel model, StoreFrame frame) {
        this.model = model;
        this.frame = frame;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if(!model.getCONNECTION_MANAGER().initializeDatabase())
            frame.handleMessage("Inventory failed to initialize!");
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
