
export class TrainingPlanRequest {
    objectives: string;
    description: string;
    exercises: string;
    frequency: string;
    duration: string;
  
    constructor(
      objectives: string,
      description: string,
      exercises: string,
      frequency: string,
      duration: string
    ) {
      this.objectives = objectives;
      this.description = description;
      this.exercises = exercises;
      this.frequency = frequency;
      this.duration = duration;
    }
  }


  export class TrainingPlanResponse {
    id: number;
  
    constructor(id: number) {
      this.id = id;
    }
  }
  