package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground. and shoots!!
*/
public class Grub extends Creature {
	
//	public float wait = 0;
//    private float dx = 0;

    public Grub(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }
//    public void setVelX(float x) {
//    	if(wait == 0)
//    	{
//    		wait ++;
//    	}
//    	else if(wait> 1000)
//    	{
//    		dx = x;
//    		super.setVelocityX(x);
//    	}
//    }

    public float getMaxSpeed() {
        return 0.05f;
    }

}
