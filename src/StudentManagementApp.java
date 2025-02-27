// Main class to run the application
public class StudentManagementApp {
    public static void main(String[] args) {
        // Create and display the frame
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StudentManagementFrame frame = new StudentManagementFrame();
                frame.setVisible(true);
            }
        });
    }
}