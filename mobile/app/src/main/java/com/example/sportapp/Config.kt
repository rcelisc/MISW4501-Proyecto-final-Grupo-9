package com.example.sportapp

object Config {

    //private const val BASE_IP = "http://192.168.1.7:"
    private const val BASE_IP = "http://192.168.58.100:"
    const val BASE_URL_Events = "${BASE_IP}3001/"
    const val BASE_URL_EVENTS_QUERIES = "${BASE_IP}3002/"
    const val BASE_URL_SERVICE = "${BASE_IP}3005/"
    const val BASE_URL_FTPVo2 = "${BASE_IP}3004/"
    const val BASE_URL_Trainings = BASE_URL_FTPVo2
    const val BASE_URL_TrainingPlans = "${BASE_IP}3003/"
    const val BASE_URL_TrainingsSessions = BASE_URL_TrainingPlans
    const val BASE_URL_USERS = "${BASE_IP}3006/"
    const val BASE_URL_USERS_QUERIES = "${BASE_IP}3007/"

//    const val BASE_URL_Events = "http://35.232.6.198/"
//    const val BASE_URL_FTPVo2 = "http://35.232.6.198/"
//    const val BASE_URL_Trainings = "http://35.232.6.198/"
//    const val BASE_URL_TrainingPlans = "http://35.232.6.198/"
//    const val BASE_URL_TrainingsSesions = "http://35.232.6.198/"



}