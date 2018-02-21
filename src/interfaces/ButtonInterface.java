package interfaces;

import java.net.URL;

import javafx.scene.media.AudioClip;
import simulation.SimGlobals;
import simulation.engine.Engine;
import simulation.engine.Message;
import simulation.engine.MessageHandler;

public class ButtonInterface
{
    Helper helper = new Helper();
    private static boolean _isDown;

    public ButtonInterface()
    {
        Engine.getMessagePump().signalInterest(SimGlobals.ACTIVATE_BRAKE, helper);
        Engine.getMessagePump().signalInterest(SimGlobals.DEACTIVATE_BRAKE,helper);
    }

    static public void setColor(ButtonColorTypes c)
    {
        Engine.getMessagePump().sendMessage(new Message(SimGlobals.SET_BUTTON_COLOR, c));
    }
    static public void play(ButtonSoundTypes s)
    {
        URL url = ButtonInterface.class.getResource(s.toString());
        AudioClip sound = new AudioClip(url.toExternalForm());
        sound.play(1, 0, 1, 0, 1);
    }

    public static boolean isDown()
    {
        return _isDown;
    }

    class Helper implements MessageHandler
    {
        @Override
        public void handleMessage(Message message)
        {
            switch (message.getMessageName())
            {
                case SimGlobals.ACTIVATE_BRAKE:
                    System.out.println("active");
                    _isDown = true;
                    break;
                case SimGlobals.DEACTIVATE_BRAKE:
                    System.out.println("unactive");
                    _isDown = false;
                    break;
            }
        }
    }

}
