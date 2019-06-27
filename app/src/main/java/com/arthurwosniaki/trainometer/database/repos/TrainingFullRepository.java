package com.arthurwosniaki.trainometer.database.repos;

public class TrainingFullRepository {
//
//    private DatabaseCallback callback;
//    private TrainingFullDao mDao;
//
//    public TrainingFullRepository(Application application) {
//        TrainometerDatabase db = TrainometerDatabase.getInstance(application);
//
//        mDao = db.trainingFullDao();
//    }
//
//    public TrainingFullRepository(Application application, DatabaseCallback callback) {
//        TrainometerDatabase db = TrainometerDatabase.getInstance(application);
//
//        mDao = db.trainingFullDao();
//        this.callback = callback;
//    }
//
//
//    public void insert(TrainingFull trainingFull){
//        new InsertTask(mDao, callback).execute(trainingFull);
//    }
//
//
//    private static class InsertTask extends AsyncTask<TrainingFull, Void, Long>{
//        private TrainingFullDao mDao;
//        private DatabaseCallback callback;
//
//        InsertTask (TrainingFullDao trainingFullDao, DatabaseCallback callback){
//            mDao = trainingFullDao;
//
//            this.callback = callback;
//        }
//
//        @Override
//        protected Long doInBackground(TrainingFull... trainingFulls) {
//            return mDao.insertTrainingFull(trainingFulls[0]);
//        }
//
//        @Override
//        protected void onPostExecute(Long aLong) {
//            super.onPostExecute(aLong);
//
//            if(aLong >= 0)
//                callback.onItemAdded();
//            else
//                callback.onDataNotAvailable();
//        }
//    }
}
