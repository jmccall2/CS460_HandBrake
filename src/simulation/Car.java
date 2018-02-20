package simulation;

import interfaces.GearInterface;
import interfaces.GearTypes;
import interfaces.SpeedInterface;
import javafx.scene.paint.Color;
import simulation.engine.*;

public class Car extends RenderEntity
{

    Helper helper = new Helper();
    private Animation _animationSequence;
    private double speed;
    private GearTypes gear;
    //acceleration due to engine, max ~ 5 m/s^2
    private boolean _isActive;
    private boolean _simulationOn = true;
    private double applied_brake_force = 0;
    private double actual_brake_force;
    private double brake_percentage;
    private boolean pressure_set = false;
    private boolean first_start = false;
    private boolean sim_is_active = false;
    private double idle_a;
    private int START_Y = 215;
    private double _wobbleMinInput = 0.0;
    private double _wobbleMaxInput = Math.PI*2;
    private double _wobbleCurrentInput = _wobbleMinInput;
    private double _wobbleInputStepSize = _wobbleMaxInput / 500;

    private BarEntity _SpeedGauge;
    private BarEntity _PressureGauge;

    private static final double mass = 1600; // in kg
 //   private static final double h = 1.0/60; // update rate
    private static final double drag_c = 2; // drag coefficient
    private boolean _startTractionLossAnimation = false;
    private static final double uk = .68; // coefficient of kinetic friction
    private static final double us = .9; // coefficient of static friction
    private static final double friction_threshold = us * 9.81 * mass;
    private GUI guiRef;


    public Car()
    {
         _animationSequence = new Animation(this, 0);
        _buildFrames();
        setLocationXYDepth(0, START_Y, -1);
        setSpeedXY(speed, 0);
        setWidthHeight(200, 100);
        Engine.getMessagePump().signalInterest(SimGlobals.ACTIVATE_BRAKE, helper);
        Engine.getMessagePump().signalInterest(SimGlobals.DEACTIVATE_BRAKE,helper);
        Engine.getMessagePump().signalInterest(SimGlobals.SET_PRESSURE,helper);
        Engine.getMessagePump().signalInterest(SimGlobals.GEAR_CHANGE,helper);
        Engine.getMessagePump().signalInterest(SimGlobals.START_SIM,helper);
        Engine.getMessagePump().signalInterest(SimGlobals.STOP_SIM,helper);

        _SpeedGauge = new BarEntity(Color.GREEN,22,625,3,0,0,75,240, BarEntityModes.SPEED);
        _SpeedGauge.setAsStaticActor(true);
        _SpeedGauge.addToWorld();

        _PressureGauge = new BarEntity(Color.GREEN,902,625,3,0,0,75,240, BarEntityModes.PRESSURE);
        _PressureGauge.setAsStaticActor(true);
        _PressureGauge.addToWorld();

    }
    
    public void setGUI(GUI gui)
    {
      this.guiRef = gui;
    }

    private void _buildFrames()
    {
        for(int i = 1; i <= 13; i++) _animationSequence.addAnimationFrame("car_drive", "resources/img/car/car" + i + ".png");
        _animationSequence.setCategory("car_drive");
    }

    private void update(double deltaSeconds){
        deltaSeconds=0.0217;
        if(!sim_is_active) return;

//        System.out.println("first start: " + first_start);
        if(first_start) {
            if (gear == GearTypes.REVERSE) {
                idle_a = -(float) ((9.0f * (drag_c / mass)) + (9.81f * .02f));
//                speed = -speed;
            } else if (gear == GearTypes.NEUTRAL) {
                idle_a = 0.0f;
            } else if (gear == GearTypes.DRIVE) {
                idle_a = (float) (9.0f * (drag_c / mass)) + (9.81f * .02f);
            } else if (gear == GearTypes.PARK) {
                idle_a = 0.0f;
            }
        }
//        System.out.println(speed);

        // todo disallow negative numbers
        int speedMod = 1;
        if(speed < 0) speedMod = -1;
        if(speed == 0) speedMod = 0;

        applied_brake_force  = 167* brake_percentage;

        if (applied_brake_force < friction_threshold) actual_brake_force = applied_brake_force;
        else actual_brake_force = uk * mass * 9.81;

        if(_isActive) {
            if (applied_brake_force > friction_threshold - 4000) _startTractionLossAnimation = true;
            else _startTractionLossAnimation = false;
        }else _startTractionLossAnimation = false;

        double actual_acceleration;

        int brake;

        int rolling_friction = 1;

        brake = 1;
        if(speed==0){
            rolling_friction = 0;
            brake = 0;
        }

        if(!_isActive) brake = 0;

        double drag_c_ = drag_c;
        if(Math.abs(speed) < 2) drag_c_ = 0;

        actual_acceleration = speedMod*(-(drag_c_ * Math.pow(speed,2))/mass - brake*(actual_brake_force / mass) - rolling_friction*(.02 * 9.81))+idle_a;

        double nextSpeed = speed + actual_acceleration * deltaSeconds;
//        System.out.println("acc :" + actual_acceleration);

        if(brake == 1){
            if(speed <= 0 && nextSpeed > 0)speed = 0;
            else if(speed >= 0 && nextSpeed < 0)speed = 0;
            else speed= nextSpeed;

        } else
        {
            speed= nextSpeed;
        }

        double speedToDisplay = speed/0.448;
        guiRef.setSpeed(speedToDisplay);
        guiRef.setPressure(brake_percentage);
    }


    public boolean running()
    {
        return _simulationOn;
    }


    private void _wobble()
    {
        double wobblePeriod;
        double wobble;
        _wobbleCurrentInput+=_wobbleInputStepSize;
        if(_wobbleCurrentInput > _wobbleMaxInput) _wobbleCurrentInput = _wobbleMinInput;
        if(speed < 63 && speed > 50) wobblePeriod = 1;
        else if(speed < 50 && speed > 35) wobblePeriod = 5;
        else if (speed < 35 && speed > 20) wobblePeriod = 10;
        else if (speed < 20 && speed > 10) wobblePeriod = 15;
        else wobblePeriod = 20;
        wobble  =(speed/20)*Math.sin(wobblePeriod*_wobbleCurrentInput);
        setRotation(wobble);
        setLocationXYDepth(getLocationX(), getLocationY() + wobble, -1);
    }


    int xOffset = 0;
    @Override
    public void pulse(double deltaSeconds) {
        if(_simulationOn) {
            _animationSequence.update(deltaSeconds); // Make sure we call this!
            update(deltaSeconds);
            Engine.getMessagePump().sendMessage(new Message(SimGlobals.SPEED, speed));
            setSpeedXY(speed * 45, 0);
            _animationSequence.setAnimationRate(1.91 / (13 * ((speed == 0) ? 0.0001 : speed)));
            _SpeedGauge.updateState(speed);
            _PressureGauge.updateState(brake_percentage);
            if (speed > 5 && _startTractionLossAnimation) {
                new TireTrack(this.getLocationX() + xOffset, this.getLocationY() + 55, 1).addToWorld();
                _wobble();
            }
        }

    }
    
    class Helper implements MessageHandler
    {
        @Override
        public void handleMessage(Message message)
        {
            switch (message.getMessageName())
            {
                case SimGlobals.GEAR_CHANGE:
                    gear = (GearTypes) message.getMessageData();
//                    System.out.println("set gear");
                    if(sim_is_active) first_start = true;
		            break;
                case SimGlobals.SET_PRESSURE:
                    brake_percentage = (Double) message.getMessageData();
                    if(brake_percentage != 0 && _isActive) {
                        pressure_set = true;
//                        System.out.println("set here");
                        if(!first_start) first_start = true;
                    }
		            break;
                case SimGlobals.START_SIM:
                    speed = SpeedInterface.getSpeed();
                    gear = GearInterface.getGear();
//                    System.out.println("##### speed: "+ speed);
                    if(gear == GearTypes.REVERSE){
                        idle_a = -(float)((speed*speed*(drag_c/mass)) + (9.81f*.02f));
                    } else if(gear == GearTypes.NEUTRAL){
                        idle_a = 0.0f;
                    } else if(gear == GearTypes.DRIVE){
                        idle_a = (float)(speed*speed*(drag_c/mass)) + (9.81f*.02f);
                    } else if(gear == GearTypes.PARK){
                        idle_a = 0.0f;
                    }
//                    first_start = false;
//                    pressure_set = false;
//                    _isActive = false;
                    sim_is_active = true;
                    _simulationOn = true;
                    break;
                case SimGlobals.ACTIVATE_BRAKE:
//                    System.out.println("activate brake");
                    _isActive = true;
                    break;
                case SimGlobals.DEACTIVATE_BRAKE:
                    _isActive = false;
                    pressure_set = false;
                    break;
                case SimGlobals.STOP_SIM:
                    _simulationOn = false;
//                    first_start = false;
                    sim_is_active = false;
//                    speed = 0;
//                    pressure_set = false;
//                    _isActive = false;
            }
        }
    }
}

