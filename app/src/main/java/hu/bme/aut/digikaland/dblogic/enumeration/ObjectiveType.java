package hu.bme.aut.digikaland.dblogic.enumeration;

import hu.bme.aut.digikaland.entities.objectives.CustomAnswerObjective;
import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.entities.objectives.PhysicalObjective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.entities.objectives.TrueFalseObjective;
import hu.bme.aut.digikaland.entities.objectives.solutions.CustomAnswerSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.MultipleChoiceSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.PhysicalSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.PictureSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.TrueFalseSolution;

/**
 * A feladatok típusai ezek lehetnek.
 */
public enum ObjectiveType {
    TrueFalse,
    MultipleChoice,
    CustomAnswer,
    Picture,
    Physical;

    /**
     * A típushoz tartozó feladat osztályt visszaadja.
     * @return A megfelelő feladat osztály a típushoz.
     */
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

    /**
     * A típushoz tartozó megoldás osztályt visszaadja.
     * @return A megfelelő megoldás oszátly a típushoz.
     */
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
}
