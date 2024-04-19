from ..models.training_plan import TrainingPlan, TrainingPlanSchema

class GetTrainingPlanQueryHandler:
    def __init__(self):
        self.schema = TrainingPlanSchema()

    def handle(self, plan_id):
        plan = TrainingPlan.query.get_or_404(plan_id)
        return self.schema.dump(plan)

    def handleAll(self):
        plans = TrainingPlan.query.all()
        return self.schema.dump(plans, many=True)
    
class GetTrainingPlansByProfileQueryHandler:
    def __init__(self):
        self.schema = TrainingPlanSchema(many=True)

    def handle(self, profile):
        training_plans = TrainingPlan.query.filter_by(profile_type=profile).all()
        return self.schema.dump(training_plans)