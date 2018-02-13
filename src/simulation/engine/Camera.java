package simulation.engine;

import simulation.engine.math.Vector3;

/**
 * Represents a virtual camera which can be attached
 * to a RenderEntity. This will calculate the transforms
 * necessary to keep the player in focus but move the world
 * around them.
 *
 * If a virtual camera is set to be the focus of the window,
 * what it "sees" will be what is drawn on the screen.
 *
 * If no camera is set then the system will pick some location to
 * focus on.
 *
 * @author Justin Hall
 */
public class Camera {
    private RenderEntity _attachedTo;
    private Vector3 _worldTranslate = new Vector3(0.0);
    private Vector3 _editedEntityLocation = new Vector3(0.0);
    private double _widthScalar = 2.5;
    private double _heightScalar = 3.0;

    /**
     * Returns the entity that this camera was attached to
     */
    public RenderEntity getEntity()
    {
        return _attachedTo;
    }

    /**
     * Attaches this camera to the given entity
     */
    public void attachToEntity(RenderEntity entity)
    {
        _attachedTo = entity;
    }

    /**
     * Sets this camera to be the main camera for
     * the entire scene, meaning what it sees will
     * be rendered on the screen
     */
    public void setAsMainCamera()
    {
        Singleton.engine.getMessagePump().sendMessage(new Message(Singleton.SET_MAIN_CAMERA, this));
    }

    /**
     * Returns the modified entity location after it has been transformed
     * to sit in the center of the screen.
     */
    public Vector3 getEntityLocation()
    {
        if (_attachedTo == null)
        {
            _editedEntityLocation.setXYZ(0.0, 0.0, 0.0);
            return _editedEntityLocation;
        }
        int scrWidth = Singleton.engine.getConsoleVariables().find(Singleton.SCR_WIDTH).getcvarAsInt();
        int scrHeight = Singleton.engine.getConsoleVariables().find(Singleton.SCR_HEIGHT).getcvarAsInt();
        _editedEntityLocation.setXYZ(scrWidth / _widthScalar, scrHeight / _heightScalar, 0.0);
        return _editedEntityLocation;
    }

    /**
     * This returns a vector representing what every object in the world
     * needs to get translated by to still be correct in their distances
     * from the main entity's location. This is because the main entity
     * who is being followed by a camera who is in focus is _always_ drawn
     * at the center of the screen, no matter where in the world they are.
     */
    public Vector3 getWorldTranslate()
    {
        if (_attachedTo == null)
        {
            _worldTranslate.setXYZ(0.0, 0.0, 0.0);
            return _worldTranslate;
        }
        double scrWidth = Singleton.engine.getConsoleVariables().find(Singleton.SCR_WIDTH).getcvarAsInt() / _widthScalar;
        double scrHeight = Singleton.engine.getConsoleVariables().find(Singleton.SCR_HEIGHT).getcvarAsInt() / _heightScalar;
        double locX = _attachedTo.getLocationX();
        double locY = _attachedTo.getLocationY();
        double modLocX = locX < 0 ? -locX : locX;
        double modLocY = locY < 0 ? -locY : locY;
        double newLocX = modLocX - scrWidth;
        double newLocY = modLocY - scrHeight;
        newLocX *= -1;
        newLocY *= -1;
        _worldTranslate.setXYZ(newLocX, newLocY, 1.0);
        return _worldTranslate;
    }
}