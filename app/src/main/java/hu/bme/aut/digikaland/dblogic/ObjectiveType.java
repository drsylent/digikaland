package hu.bme.aut.digikaland.dblogic;

import hu.bme.aut.digikaland.entities.objectives.CustomAnswerObjective;
import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.PhysicalObjective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.entities.objectives.TrueFalseObjective;
import hu.bme.aut.digikaland.entities.objectives.solutions.CustomAnswerSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.MultipleChoiceSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.PhysicalSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.PictureSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.TrueFalseSolution;

/**
 * Created by Sylent on 2018. 04. 08..
 */

public enum ObjectiveType {
    TrueFalse,
    MultipleChoice,
    CustomAnswer,
    Picture,
    Physical;

    public Class<?> getObjectiveClass(){
        switch (this){
            case Picture: return PictureObjective.class;
            case Physical: return PhysicalObjective.class;
            case TrueFalse: return TrueFalseObjective.class;
            case CustomAnswer: return CustomAnswerObjective.class;
            case MultipleChoice: return MultipleChoiceObjective.class;
            default: return null;
        }
    }

    public Class<?> getSolutionClass(){
        switch (this){
            case Picture: return PictureSolution.class;
            case Physical: return PhysicalSolution.class;
            case TrueFalse: return TrueFalseSolution.class;
            case CustomAnswer: return CustomAnswerSolution.class;
            case MultipleChoice: return MultipleChoiceSolution.class;
            default: return null;
        }
    }

    public static ObjectiveType getObjectiveType(Objective obj){
        if(obj instanceof TrueFalseObjective) return TrueFalse;
        if(obj instanceof MultipleChoiceObjective) return MultipleChoice;
        if(obj instanceof CustomAnswerObjective) return CustomAnswer;
        if(obj instanceof PictureObjective) return Picture;
        if(obj instanceof PhysicalObjective) return Physical;
        return null;
    }
}
