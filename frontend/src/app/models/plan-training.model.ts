
export class TrainingPlanRequest {
    objectives: string;
    description: string;
    exercises: string;
    frequency: string;
    duration: string;
    profile_type: string;
  
    constructor(
      objectives: string,
      description: string,
      exercises: string,
      frequency: string,
      duration: string, 
      profile_type: string
    ) {
      this.objectives = objectives;
      this.description = description;
      this.exercises = exercises;
      this.frequency = frequency;
      this.duration = duration;
      this.profile_type = profile_type;
    }
  }


  export class TrainingPlanResponse {
    id: number;
  
    constructor(id: number) {
      this.id = id;
    }
  }
  