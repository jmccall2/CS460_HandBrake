package simulation;

import simulation.engine.RenderEntity;

public class SingleFrameEntity extends RenderEntity
{
    public SingleFrameEntity(String texture, int x, int y, int d, int xs, int ys, int w, int h)
    {
        setTexture(texture);
        setLocationXYDepth(x, y,d);
        setSpeedXY(xs, ys);
        setWidthHeight(w, h);
    }


    @Override
    public void pulse(double deltaSeconds) {

    }
}
