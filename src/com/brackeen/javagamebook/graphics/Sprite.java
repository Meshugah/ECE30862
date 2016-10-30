package com.brackeen.javagamebook.graphics;

import java.awt.Image;

public class Sprite {

    protected Animation anim;
    // position (pixels) THIS IS GOOD
    private float x;
    private float y;
    // velocity (pixels per millisecond)
    private float dx;
    private float dy;

    static private int health = 20;
    //health stat
    private int hcount = 0;
    //health counter
    private float wait;
    public boolean win = false;
    //winning state
    private static final int DIE_TIME = 1000;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;

    /**
        Creates a new Sprite object with the specified Animation.
    */
    public Sprite(Animation anim) {
        this.anim = anim;
    }
    
    //sets win state
    public void win(){
    	win = true;
    }
    
    //checks win state
    public boolean iswin(){
    	return win;
    }
    
    //resets win state
    public void lose(){
    	win = false;
    }
    
    //checks health
    public int Health() {
    	return health;
    }
    
    //initialize health
    public void healthint() {
    	health = 20;
    }
    
    //increase health while moving
    public void hCount(){
    	hcount++;
    	if(hcount>25){
    		hcount = 0;
    		Health(1);
    	}
    }
    
    //increase health by x
    public int  Health(int x){
    	health +=x;
    	if(health > 40){health=40;}
    	return health;
    }

    /**
        Updates this Sprite's Animation and its position based
        on the velocity.
    */
    public void update(long elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        anim.update(elapsedTime);
    }

    /**
        Gets this Sprite's current x position.
    */
    public float getX() {
        return x;
    }

    /**
        Gets this Sprite's current y position.
    */
    public float getY() {
        return y;
    }

    /**
        Sets this Sprite's current x position.
    */
    public void setX(float x) {
        this.x = x;
    }

    /**
        Sets this Sprite's current y position.
    */
    public void setY(float y) {
        this.y = y;
    }

    /**
        Gets this Sprite's width, based on the size of the
        current image.
    */
    public int getWidth() {
        return anim.getImage().getWidth(null);
    }

    /**
        Gets this Sprite's height, based on the size of the
        current image.
    */
    public int getHeight() {
        return anim.getImage().getHeight(null);
    }

    /**
        Gets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityX() {
        return dx;
    }

    /**
        Gets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityY() {
        return dy;
    }

    /**
        Sets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityX(float dx) {
        this.dx = dx;
    }

    /**
        Sets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityY(float dy) {
        this.dy = dy;
    }

    /**
        Gets this Sprite's current image.
    */
    public Image getImage() {
        return anim.getImage();
    }

    /**
        Clones this Sprite. Does not clone position or velocity
        info.
    */
    public Object clone() {
        return new Sprite(anim);
    }
    public float getMaxSpeed() {
        return 0.5f;
    }
    
    //does nothing
    public void waiting(long elapsedTime){
    	if(wait == -1){
    		wait = elapsedTime;
    	}
    	else if((elapsedTime - wait) > 1000){
    		wait = elapsedTime;
    		Health(5);
    	}
    }
    
   //does nothing
    public void stopWaiting(){
    	wait = -1;
    }
}
