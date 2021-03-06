package com.brackeen.javagamebook.tilegame.sprites;

import java.lang.reflect.Constructor;
import com.brackeen.javagamebook.graphics.*;

/**
    A Creature is a Sprite that is affected by gravity and can
    die. It has four Animations: moving left, moving right,
    dying on the left, and dying on the right.
*/
public abstract class Creature extends Sprite {

    /**
     Amount of time to go from STATE_DYING to STATE_DEAD.
     */
    private static final int DIE_TIME = 1000;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;
    public float wait = 0;
    private float waitx = -1;

    private Animation left;
    private Animation right;
    private Animation deadLeft;
    private Animation deadRight;
    private int state;
    private long stateTime;
    public boolean face_right = true; //player always face right (init)
    public boolean face_left = true; //bugs always face left (init)
    public long bugsct = System.currentTimeMillis();
    public int evilsct = 0;

    /**
     Creates a new Creature with the specified Animations.
     */
    public Creature(Animation left, Animation right,
                    Animation deadLeft, Animation deadRight)
    {
        super(right);
        this.left = left;
        this.right = right;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;
        state = STATE_NORMAL;
    }

    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                    (Animation)left.clone(),
                    (Animation)right.clone(),
                    (Animation)deadLeft.clone(),
                    (Animation)deadRight.clone()
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


    /**
     Gets the maximum speed of this Creature.
     */
    public float getMaxSpeed() {
        return 0;
    }


    /**
     Wakes up the creature when the Creature first appears
     on screen. Normally, the creature starts moving left.
     */
    public void wakeUp() {
        if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }


    /**
     Gets the state of this Creature. The state is either
     STATE_NORMAL, STATE_DYING, or STATE_DEAD.
     */
    public int getState() {
        return state;
    }


    /**
     Sets the state of this Creature to STATE_NORMAL,
     STATE_DYING, or STATE_DEAD.
     */
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }


    /**
     Checks if this creature is alive.
     */
    public boolean isAlive() {
        return (state == STATE_NORMAL);
    }


    /**
     Checks if this creature is flying.
     */
    public boolean isFlying() {
        return false;
    }


    /**
     Called before update() if the creature collided with a
     tile horizontally.
     */
    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }


    /**
     Called before update() if the creature collided with a
     tile vertically.
     */
    public void collideVertical() {
        setVelocityY(0);
    }

    public int isWaiting(float x) {
    	if(wait == 0){
    		wait = System.currentTimeMillis();
    	}
    	if(System.currentTimeMillis()-wait > 1000){
    		return 1;
    	}
    	if(waitx != -1){
    		waitx = x;
    	}
    	if(waitx - x == 120){
    		return 1;
    	}
    	return 0;
    }
    /**
     Updates the animaton for this creature.
     */
    public void update(long elapsedTime) {
        // select the correct Animation
        Animation newAnim = anim;
        if (getVelocityX() < 0) {
            newAnim = left;
            face_right = false;
            face_left = true;
        }
        else if (getVelocityX() > 0) {
            newAnim = right;
            face_right = true;
            face_left = false;
        }
        if (state == STATE_DYING && newAnim == left) {
            newAnim = deadLeft;
        }
        else if (state == STATE_DYING && newAnim == right) {
            newAnim = deadRight;
        }

        // update the Animation
        if (anim != newAnim) {
            anim = newAnim;
            anim.start();
        }
        else {
            anim.update(elapsedTime);
        }

        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
    }
    public double travel_length = 0;
    public final double range = 50;
    public final double bug_range = 50;
    public double travel_accumulation(float newX){
        travel_length += newX;
        if(travel_length >= range){
            travel_length = range;
        }
        return travel_length;
    }
    public double travel_accumulation_bug(float newX){
        travel_length += newX;
        if(travel_length >= bug_range){
            travel_length = bug_range;
        }
        return travel_length;
    }
}