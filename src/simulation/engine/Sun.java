package simulation.engine;

public class Sun extends RenderEntity
{
    private Animation _animationSequence;

    public Sun()
    {
        _animationSequence = new Animation(this, .5);
        _buildFrames();
        setLocationXYDepth(-350,5,8);
        setSpeedXY(50,0);
        setWidthHeight(100, 100);
    }


    private void _buildFrames()
    {
        for(int i = 1; i <= 4; i++) _animationSequence.addAnimationFrame("sun", "resources/img/world/sun" + i + ".png");
        for(int i = 4; i >= 1; i--) _animationSequence.addAnimationFrame("sun", "resources/img/world/sun" + i + ".png");
        _animationSequence.setCategory("sun");
    }

    @Override
    public void pulse(double deltaSeconds)
    {
        _animationSequence.update(deltaSeconds);
    }
}
