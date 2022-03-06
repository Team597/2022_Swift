package frc.robot.Utilities;

public class ToggleButton {
    boolean toggleState;
    boolean lastCheck;

    public ToggleButton() {
        // gives the value of false to toggleState
        toggleState = false;
        lastCheck = false;
    }
    //forcefully sets the values
    public void Force(boolean value) {
        toggleState = value;
        lastCheck = value;
    }
    // Utilizes value to change the toggle state from true to false and send
    // the value out when this it is called
    public boolean Input(boolean value) {
        // if value is not equal to last check and value is true, the
        // following code is runs
        if (value && value != lastCheck) {
            // sets the value of toggle state equal to the opposite value of
            // toggle state
            toggleState = !toggleState;
        }
        // sets last check equal to value so that it only toggles once
        lastCheck = value;
        return toggleState; 
    }
    
    public boolean Output() {
        // sends out the current value of toggle State
        return toggleState;
    }

}
