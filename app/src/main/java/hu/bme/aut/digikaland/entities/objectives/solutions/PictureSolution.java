package hu.bme.aut.digikaland.entities.objectives.solutions;

import java.util.ArrayList;

import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.PictureEvaluateFragment;

public class PictureSolution extends Solution {
    public ArrayList<String> getAnswer() {
        return answer;
    }

    public void setAnswers(ArrayList<String> answer) {
        this.answer = answer;
    }

    private ArrayList<String> answer;

    public void uriizeFilePaths(){
        ArrayList<String> newanswer = new ArrayList<>();
        for(String file : answer){
            newanswer.add("file:///" + file);
        }
        answer = newanswer;
    }

    public PictureSolution(int curr, int max){
        super(curr, max);
    }

    @Override
    public PictureEvaluateFragment createFragment() {
        return PictureEvaluateFragment.newInstance(this, getCurrentPoints(), getMaxPoints());
    }
}
