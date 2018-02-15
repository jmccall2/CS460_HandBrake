package simulation.engine;

/**
 * Represents an individual console variable and
 * corresponding helper functions.
 *
 * @author Justin Hall
 */
public class ConsoleVariable {
    private String _cvarName;
    private String _defaultValue;
    private String _cvarValue; // Raw String value
    private int _cvarIntVal; // Defaults to -1 if _cvarValue cannot be casted
    private double _cvarFloatVal; // Defaults to -1.0 if _cvarValue cannot be casted
    private int _numEdits = 0; // Number of times this variable was edited

    public ConsoleVariable(String name, String defaultValue)
    {
        _cvarName = name;
        //Singleton.simulation.engine.getMessagePump().registerMessage(new Message(_cvarName + "_WAS_CHANGED"));
        _defaultValue = defaultValue;
        _setValueNoMessageDispatch(_defaultValue);
    }

    public ConsoleVariable(String name, String defaultValue, String value)
    {
        _cvarName = name;
        _defaultValue = defaultValue;
        _setValueNoMessageDispatch(value);
    }

    /**
     * Resets the console variable to its default value
     */
    public void reset()
    {
        setValue(_defaultValue);
    }

    /**
     * @return total number of times this console variable has been edited
     */
    public int getEditCount()
    {
        return _numEdits;
    }

    /**
     * Gets the cvar name
     */
    public String getcvarName()
    {
        return _cvarName;
    }

    /**
     * Gets the raw cvar value as a string (Ex: "127.26")
     */
    public String getcvarValue()
    {
        return _cvarValue;
    }

    public String getcvarDefault()
    {
        return _defaultValue;
    }

    /**
     * Gets cvar value as int (Ex: 127)
     */
    public int getcvarAsInt()
    {
        return _cvarIntVal;
    }

    /**
     * Gets cvar value as double (Ex: 127.26)
     * @return
     */
    public double getcvarAsFloat()
    {
        return _cvarFloatVal;
    }

    /**
     * Sets the value of the console variable (Ex: "127")
     */
    public void setValue(String value)
    {
        _setValueNoMessageDispatch(value);
        // Notify anyone who is interested that this variable was changed
        //Singleton.simulation.engine.getMessagePump().sendMessage(_cvarName + "_WAS_CHANGED");
    }

    /**
     * This determines what the cvar resets to if reset() is called
     */
    public void setDefault(String defaultValue)
    {
        _defaultValue = defaultValue;
    }

    private void _setValueNoMessageDispatch(String value)
    {
        ++_numEdits;
        _cvarValue = value;
        try
        {
            _cvarIntVal = Integer.parseInt(_cvarValue);
            _cvarFloatVal = Double.parseDouble(_cvarValue);
        }
        catch (Exception e)
        {
            _cvarIntVal = -1;
            _cvarFloatVal = -1.0;
        }
    }

    @Override
    public int hashCode() {
        return _cvarName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConsoleVariable && ((ConsoleVariable)obj)._cvarName.equals(_cvarName);
    }

    @Override
    public String toString() {
        return "name: " + _cvarName + "; value: " + _cvarValue;
    }
}
