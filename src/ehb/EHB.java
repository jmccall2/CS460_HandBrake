package ehb;

import interfaces.*;

import java.util.Map;
import java.util.TreeMap;


public class EHB
{

  private double _speed;
  private GearTypes _gear;
  private boolean _isActive;

  //Max pressure is considered to be at 6 kPa.
  //first is speed. second is pressure
//  static Map<Integer, Integer> goodPressureProfile;
  static TreeMap<Long, Integer> goodPressureProfile;

  static
  {
    goodPressureProfile = new TreeMap<Long, Integer>();
    goodPressureProfile.put(Long.valueOf(20), 2);
    goodPressureProfile.put(Long.valueOf(30), 3);
    goodPressureProfile.put(Long.valueOf(40), 4);
    goodPressureProfile.put(Long.valueOf(50), 4);
    goodPressureProfile.put(Long.valueOf(60), 5);
    goodPressureProfile.put(Long.valueOf(70), 6);
    goodPressureProfile.put(Long.valueOf(80), 6);
    goodPressureProfile.put(Long.valueOf(90), 5);
    goodPressureProfile.put(Long.valueOf(100), 5);
  }
  //The plot is not supposed to be perfectly linear. These values only serve as
  //an approximation of what the graph should be.

  //We use the pressure profile obtained from http://www.scielo.br/scielo.php?script=sci_arttext&pid=S0100-73862001000100007
  // with supplement http://www.optimumg.com/docs/Brake_tech_tip.pdf

  public EHB()
  {
    init();
  }

  public void init()
  {
    ButtonInterface.setColor(ButtonColorTypes.BLUE);
    _isActive = false;
  }

  //Add timer to class to demo how they can measure. time between button clicks.

  public void update()
  {
    if (ButtonInterface.isDown()) {
        ButtonInterface.setColor(ButtonColorTypes.RED);
        ButtonInterface.play(ButtonSoundTypes.ENGAGED);

        _speed = SpeedInterface.getSpeed(); // Get the speed from the speed interface.
        _gear = GearInterface.getGear();  // Get the current gear from the Gear interface.


        if (_gear.toString().equals("Park")) {
            BrakeInterface.setPressure(100.00);
        } else if ((!_gear.toString().equals("Reverse")) && _speed < 0) {
            BrakeInterface.setPressure(100.00);
        } else {


            //This uses the max and low values of the tree map to get the closest value in the
            //pressure profile
            Long key = Long.valueOf((int) _speed);
            Map.Entry<Long, Integer> floor = goodPressureProfile.floorEntry(key);
            Map.Entry<Long, Integer> ceiling = goodPressureProfile.ceilingEntry(key);

            double closestResult;
            if (floor != null && ceiling != null) {
                closestResult = (floor.getValue() + ceiling.getValue()) / 2.0;
            } else if (floor != null) {
                closestResult = floor.getValue();
            } else {
                closestResult = ceiling.getValue();
            }

            BrakeInterface.setPressure((closestResult / 6.0) * 100);
        }
    }
    else
    {
        ButtonInterface.setColor(ButtonColorTypes.BLUE);
        ButtonInterface.play(ButtonSoundTypes.DISENGAGED);

        BrakeInterface.setPressure(0.0);
    }


  }
}
