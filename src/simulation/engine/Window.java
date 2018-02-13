package simulation.engine;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Represents the window for the game/simulation
 *
 * @author Justin Hall
 */
public class Window implements MessageHandler, PulseEntity {
    private Stage _stage;
    public Pane _stack;
    private Canvas _canvas;
    private Scene _jfxScene;
    private GraphicsContext _gc;
    private boolean _isFullscreen = false;
    private int _width = 1024;
    private int _height = 768;
    private boolean _resizeable = true;
    private String _title = "Application";

    public int getWidth()
    {
        return _width;
    }

    public int getHeight()
    {
        return _height;
    }

    public boolean isFullscreen()
    {
        return _isFullscreen;
    }

    public Scene getJFXScene()
    {
        return _jfxScene;
    }

    public GraphicsContext init(Stage stage)
    {
        // We want to update frequently to check for resizes, so tell the system to add us as a pulse entity
        Singleton.engine.getMessagePump().sendMessage(new Message(Singleton.ADD_PULSE_ENTITY, this));
        Singleton.engine.getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.SCR_FULLSCREEN, Boolean.toString(_isFullscreen)));
        Singleton.engine.getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.SCR_WIDTH, Integer.toString(_width)));
        Singleton.engine.getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.SCR_HEIGHT, Integer.toString(_height)));
        Singleton.engine.getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.SCR_RESIZEABLE, Boolean.toString(_resizeable)));
        Singleton.engine.getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.SCR_TITLE, _title));
        ConsoleVariables cvars = Singleton.engine.getConsoleVariables();
        _isFullscreen = Boolean.parseBoolean(cvars.find(Singleton.SCR_FULLSCREEN).getcvarValue());
        _width = Integer.parseInt(cvars.find(Singleton.SCR_WIDTH).getcvarValue());
        _height = Integer.parseInt(cvars.find(Singleton.SCR_HEIGHT).getcvarValue());
        _resizeable = Boolean.parseBoolean(cvars.find(Singleton.SCR_RESIZEABLE).getcvarValue());
        _title = cvars.find(Singleton.SCR_TITLE).getcvarValue();
        Singleton.engine.getMessagePump().signalInterest(Singleton.SET_SCR_WIDTH, this);
        Singleton.engine.getMessagePump().signalInterest(Singleton.SET_SCR_HEIGHT, this);
        Singleton.engine.getMessagePump().signalInterest(Singleton.SET_FULLSCREEN, this);
        Singleton.engine.getMessagePump().signalInterest(Singleton.ADD_UI_ELEMENT, this);
        Singleton.engine.getMessagePump().signalInterest(Singleton.REMOVE_UI_ELEMENT, this);
        stage.setFullScreen(_isFullscreen);
        stage.setResizable(_resizeable);
        if (_isFullscreen)
        {
            Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
            _width = (int)screenSize.getWidth();
            _height = (int)screenSize.getHeight();
        }
        //stage.setResizable(false);
        stage.setTitle(_title);
        _stage = stage;
        Group root = new Group();
        _canvas = new Canvas(_width, _height);
        _stack = new Pane();
        _stack.getChildren().addAll(_canvas);
        root.getChildren().add(_stack);
        //root.getChildren().add(_canvas);
        _jfxScene = new Scene(root, _width, _height);
        stage.setScene(_jfxScene);
        stage.show();
        _gc = _canvas.getGraphicsContext2D();
        return _gc;
    }

    @Override
    public void handleMessage(Message message) {
        // If the window width/height were changed then we need to deal with it
        if (message.getMessageName().equals("SCR_WIDTH_WAS_CHANGED") ||
                message.getMessageName().equals("SCR_HEIGHT_WAS_CHANGED")) {
            _width = (int) _jfxScene.getWidth();
            _height = (int) _jfxScene.getHeight();
            _canvas.setWidth(_width);
            _canvas.setHeight(_height);
        }
        else if (message.getMessageName().equals("FULLSCREEN_WAS_CHANGED")) {
            _stage.setFullScreen(_isFullscreen);
        }

        switch(message.getMessageName())
        {
            case Singleton.ADD_UI_ELEMENT:
            {
                _stack.getChildren().add((Node)message.getMessageData());
                break;
            }
            case Singleton.REMOVE_UI_ELEMENT:
            {
                _stack.getChildren().remove((Node)message.getMessageData());
                break;
            }
        }
    }

    @Override
    public void pulse(double deltaSeconds) {
        if (_width != (int)_jfxScene.getWidth() || _height != (int)_jfxScene.getHeight())
        {
            Singleton.engine.getConsoleVariables().find(Singleton.SCR_WIDTH).setValue(Integer.toString((int)_jfxScene.getWidth()));
            Singleton.engine.getConsoleVariables().find(Singleton.SCR_HEIGHT).setValue(Integer.toString((int)_jfxScene.getHeight()));
        }
    }
}
