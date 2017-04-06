package technology.tabula.tabula_web.routes;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.RouteGroup;
import technology.tabula.tabula_web.JsonTransformer;
import technology.tabula.tabula_web.background.JobExecutor;
import technology.tabula.tabula_web.background.job.DetectTables;
import technology.tabula.tabula_web.background.job.GenerateDocumentData;
import technology.tabula.tabula_web.background.job.Job;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static spark.Spark.get;

public class JobProgressRouteGroup implements RouteGroup {

    static class JobProgress {
        public String upload_id;
        public String file_id;
        public String status;
        public String message;
        public String error_type;
        public int pct_complete;

        public JobProgress(String status, String message, String error_type, int pct_complete) {
            this.status = status;
            this.message = message;
            this.error_type = error_type;
            this.pct_complete = pct_complete;
        }

        public JobProgress(String status, String message, String error_type, int pct_complete, String file_id, String upload_id) {
            this(status, message, error_type, pct_complete);
            this.file_id = file_id;
            this.upload_id = upload_id;
        }
    }

    class JobStatusJson implements Route {

        @Override
        public Object handle(Request request, Response response) throws Exception {

            String batchId = request.params(":upload_id");
            List<Job> jobs = JobExecutor.getInstance().getByBatch(batchId);

            if (jobs.isEmpty()) {
                response.status(404);
                return new JobProgress("error", "No such job", "no-such-job", 0);
            } else if (jobs.stream().anyMatch(j -> j.isFailed() && (j instanceof GenerateDocumentData))) {
                return new JobProgress("error", "Fatal Error: No text data is contained in this PDF file. Tabula can't process it.",
                        "no-text", 99);
            } else if (jobs.stream().anyMatch(Job::isFailed)) {
                return new JobProgress("error", "Sorry, your file upload could not be processed. Please double-check that the file you uploaded is a valid PDF file and try again.",
                        "unknown", 99);
            } else {
                List<Job> batch = jobs.stream().filter(j -> !((j instanceof DetectTables) && j.isWorking())).collect(Collectors.toList());
                batch.sort((j1, j2) -> Integer.compare(j1.percentComplete(), j2.percentComplete()));

                Job firstWorkingJob = batch.stream().filter(Job::isWorking).findFirst().get();

                int pctComplete = batch.stream().map(Job::percentComplete).reduce(0, (sum, pct) -> sum + pct) / batch.size();

                // TODO finish this branch
                return new JobProgress(firstWorkingJob != null ? "working" : "completed",
                        "Dummy message",
                        "",
                        pctComplete,
                        request.params(":file_id"),
                        batchId);
            }
        }
    }

    @Override
    public void addRoutes() {
        get(":upload_id/json", new JobStatusJson(), new JsonTransformer());
        get(":upload_id", (req, rsp) -> {
            return "";
        }); // TODO: implement
    }
}
