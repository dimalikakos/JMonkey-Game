/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Alikakos_Game;

import java.util.ArrayList;


/**
 *
 * @author Xenor
 */
public class wall {
    int side;
    ArrayList<wall_cube> boxes = new ArrayList<wall_cube>();
    
    public wall(int side,wall_cube box){   
        this.side=side;
        boxes.add(box);        
    }
    public wall_cube get_highest_box(){
        if(boxes.size()>0){
            return boxes.get(boxes.size()-1);
        }
        return null;
    }
    public void remove_box(wall_cube box){
        System.out.println("REMOVING: "+box.id+" OF SIDE: "+side);
        boxes.remove(box);
        System.out.println("REMAINING: "+boxes.size());
        
    }
}
