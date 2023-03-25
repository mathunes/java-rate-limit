    package utils;

    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.concurrent.ConcurrentHashMap;
    import java.util.Map;

    import javax.servlet.http.HttpServletRequest;

    public class RateLimit {

        private static final int MAX_REQUESTS = 3;
        private static final int INTERVAL = 1 * 60 * 1000; // 6000 ms || 1 min
        private static final int BLOCKED_TIME = 2 * 60 * 1000; //1200 ms || 2min

        private Map<String, ArrayList<Calendar>> requestsList;
        private Map<String, Calendar> blockedList;

        public RateLimit() {
            requestsList = new ConcurrentHashMap<String, ArrayList<Calendar>>();
            blockedList = new ConcurrentHashMap<String, Calendar>();
        }

        public boolean doFilter(HttpServletRequest request) {

            String sessionId = request.getSession().getId();
            Calendar currentTime = this.getCurrentTime();

            if (requestsList.containsKey(sessionId)) {
                if (isInBlockedList(sessionId)) {
                    return false;
                }

                if (isAnExceedingRequest(sessionId)) {
                    return false;
                }

                requestsList.get(sessionId).add(currentTime);
            } else {
                ArrayList<Calendar> calendarList = new ArrayList<Calendar>();
                calendarList.add(currentTime);

                requestsList.put(sessionId, calendarList);
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
                blockedList.put(sessionId, currentTime);

                return true;
            }

            return false;
        }

        public Boolean isInBlockedList(String sessionId) {
            Calendar currentTime = getCurrentTime();
            Calendar blockTime = blockedList.get(sessionId);

            if (blockTime == null) {
                return false;
            }

            if ((currentTime.getTimeInMillis() - blockTime.getTimeInMillis()) < BLOCKED_TIME) {
                return true;
            }

            blockedList.remove(sessionId);

            return false;
        }

        public Calendar getCurrentTime() {
            Calendar now = Calendar.getInstance();
            return now;
        }

    }
