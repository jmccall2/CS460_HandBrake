package simulation.engine;

import simulation.engine.math.Vector3;

/**
 * This class sits at the top of the hierarchy of objects
 * which can be registered with the world. Regardless of
 * the visual representation, these objects share several
 * things in common:
 *      1) They can be transformed into the camera's space
 *         in the event that a camera is being used
 *      2) They support movement and changing movement in
 *         the form of speed and acceleration
 *      3) They can be made static, meaning all updates to
 *         their location are directly related to screen space
 *         rather than camera space (i.e., they will not be
 *         pushed off the screen because the camera moved
 *         away from them)
 *
 * @author Justin Hall
 */
public abstract class Actor {
    private Vector3 _translation = new Vector3(0,0,1); // z-component should stay 1 for 2D
    private Vector3 _speed = new Vector3(0, 0, 0);
    private Vector3 _acceleration = new Vector3(0, 0, 0);
    private Vector3 _scaleWidthHeight = new Vector3(1, 1, 1);
    private double _rotationAngle = 0;
    private double _depth = 0; // NOT the same as the translation z-component
    private boolean _isVisibleOnScreen = true; // Updated by renderer
    private boolean _isStaticActor = false; // If true it will not be transformed into camera space

    /**
     * Tells the renderer whether this actor should be transformed as the camera
     * moves or if its location should always stay relative to screen pixel coordinates
     * @param value true if it should be static and not be transformed by the camera
     *              and false if it should not be static
     */
    public void setAsStaticActor(boolean value)
    {
        _isStaticActor = value;
    }

    public boolean isStaticActor()
    {
        return _isStaticActor;
    }

    /**
     * @return true if the object was visible on the screen during the
     *         last frame or if it was somewhere off screen
     */
    public boolean isVisibleOnScreen()
    {
        return _isVisibleOnScreen;
    }

    // Package private
    void setScreenVisibility(boolean value)
    {
        _isVisibleOnScreen = value;
    }

    /**
     * Adds this actor to the world so that it can be seen and interacted
     * with
     */
    public abstract void addToWorld();

    /**
     * Removes this actor from the world so that it can no longer be seen
     * or interacted with
     */
    public abstract void removeFromWorld();

    /**
     * Sets the x-y speed of the actor in feet per second
     */
    public void setSpeedXY(double speedX, double speedY)
    {
        _speed.setXYZ(speedX, speedY, 0);
    }

    /**
     * Sets the acceleration which will automatically change
     * the speed over time as expected
     */
    public void setAccelerationXY(double accelX, double accelY)
    {
        _acceleration.setXYZ(accelX, accelY, 0);
    }

    /**
     * @param x x location
     * @param y y location
     * @param depth depth, which determines which objects are in front of or behind it
     */
    public void setLocationXYDepth(double x, double y, double depth)
    {
        _translation.setXYZ(x, y, 1);
        _depth = depth;
    }

    public void setWidthHeight(double width, double height)
    {
        _scaleWidthHeight.setXYZ(width, height, 0);
    }

    public void setRotation(double angleDeg)
    {
        _rotationAngle = angleDeg;
    }

    public double getLocationX()
    {
        return _translation.x();
    }

    public double getLocationY()
    {
        return _translation.y();
    }

    public double getDepth()
    {
        return _depth;
    }

    public double getRotation()
    {
        return _rotationAngle;
    }

    public double getSpeedX()
    {
        return _speed.x();
    }

    public double getSpeedY()
    {
        return _speed.y();
    }

    public double getAccelerationX()
    {
        return _acceleration.x();
    }

    public double getAccelerationY()
    {
        return _acceleration.y();
    }

    public double getWidth()
    {
        return _scaleWidthHeight.x();
    }

    public double getHeight()
    {
        return _scaleWidthHeight.y();
    }

    /*
     * The following are package private
     */
    Vector3 getTranslationVec()
    {
        return _translation;
    }

    Vector3 getSpeedVec()
    {
        return _speed;
    }

    Vector3 getAccelerationVec()
    {
        return _acceleration;
    }

    Vector3 getScaleVec()
    {
        return _scaleWidthHeight;
    }
}