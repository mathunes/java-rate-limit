    package utils;

    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.concurrent.ConcurrentHashMap;
    import java.util.Map;

    import javax.servlet.http.HttpServletRequest;

    public class RateLimit {

        private static final int MAX_REQUESTS = 3;
        private static final int INTERVAL = 1 * 60 * 1000; // 6000 ms || 1 min

        private Map<String, ArrayList<Calendar>> requestsList;

        public RateLimit() {
            requestsList = new ConcurrentHashMap<String, ArrayList<Calendar>>();
        }

        public boolean doFilter(HttpServletRequest request) {

            String sessionId = request.getSession().getId();
            Calendar currentTime = this.getCurrentTime();

            if (requestsList.containsKey(sessionId)) {
                if (isAnExceedingRequest(sessionId)) {
                    return false;
                }

                requestsList.get(sessionId).add(currentTime);
            } else {
                ArrayList<Calendar> test = new ArrayList<Calendar>();
                test.add(currentTime);

                requestsList.put(sessionId, test);
            }

            return true;
        }

        public Boolean isAnExceedingRequest(String sessionId) {

            ArrayList<Calendar> requestsListArray = requestsList.get(sessionId);
            int requestsListArraySize = requestsListArray.size();
            Calendar currentTime = getCurrentTime();

            if (requestsListArraySize < MAX_REQUESTS) {
                return false;
            }

            int countRequestLessThanOneMinute = 0;

            // getting the last items from list
            for (int i = 0; i < MAX_REQUESTS; i++) {

                if ((currentTime.getTimeInMillis()
                        - requestsListArray.get(requestsListArraySize - (i + 1)).getTimeInMillis()) < INTERVAL) {
                    countRequestLessThanOneMinute++;
                }

            }

            if (countRequestLessThanOneMinute == MAX_REQUESTS) {
                return true;
            }

            return false;
        }

        public Calendar getCurrentTime() {
            Calendar now = Calendar.getInstance();
            return now;
        }

    }
