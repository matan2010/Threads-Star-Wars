package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;


import java.util.List;

public class AttackEvent implements Event<Boolean> {


    private Attack myAttack;
    private boolean finished;

    public AttackEvent(Attack attack) {
        myAttack = attack;
    }

    public List<Integer> getSerials() {
        return myAttack.getSerials();
    }

    public long getAttackDuration() {
        return myAttack.getDuration();
    }

    public Attack getMyAttack() {
        return myAttack;
    }
    public Boolean isFinished(){
        return isFinished();
    }
}
