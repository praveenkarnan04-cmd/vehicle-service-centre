package vsc;

import vsc.ui.ConsoleApp;
import vsc.ui.swing.SwingApp;

import java.awt.GraphicsEnvironment;

public class Main {
    public static void main(String[] args) {
        boolean console = args.length > 0 && args[0].equalsIgnoreCase("--console");
        if (console || GraphicsEnvironment.isHeadless()) {
            if (!console) System.out.println("No display detected - starting console mode.");
            new ConsoleApp().run();
        } else {
            SwingApp.launch();
        }
    }
}
