package Alikakos_Game;

/**
 *
 * @author Dimitrios, Fall 2016
 */


import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;


public class WorldDayNightControl extends AbstractControl {
    
    private float upThreshold;
    private float downThreshold;
    private DirectionalLight dl;
    private boolean upPhase = true;
    private float sum[];
    
    public WorldDayNightControl(DirectionalLight dl,  float upThreshold, float downThreshold, float sum[]) {
        this.upThreshold = upThreshold;
        this.downThreshold = downThreshold; 
        this.dl = dl;
        this.sum = sum;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (upPhase) {
            sum[0] += tpf/50;
            dl.setColor(new  ColorRGBA(1f,1f,1f,1f).multLocal(sum[0]) );
             if (sum[0] >= upThreshold) {
                upPhase = false;
            }
        } else {
            sum[0] -= tpf/50;
            dl.setColor( new ColorRGBA(1f, 1f, 1f, 1f).multLocal(sum[0]) );  
            if (sum[0] <= downThreshold) {
                upPhase = true;
            }
        }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
