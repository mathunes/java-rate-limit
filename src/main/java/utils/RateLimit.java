package utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RateLimit {

    private static final int MAX_REQUESTS = 3;
    private static final int INTERVAL = 1 * 60 * 1000; // 1min
    private static final int BLOCKED_TIME = 2 * 60 * 1000; // 2min

    private Map<String, ArrayList<Calendar>> requestsList;
    private Map<String, Calendar> blockedList;

    private static RateLimit instance;
    
    public RateLimit() {
        requestsList = new ConcurrentHashMap<String, ArrayList<Calendar>>();
        blockedList = new ConcurrentHashMap<String, Calendar>();
    }

    public static synchronized RateLimit getInstance() {
        if (instance == null) {
            instance = new RateLimit();
        }
        return instance;
    }

    public boolean doFilter(HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        System.out.println(ipAddress);
        Calendar currentTime = this.getCurrentTime();

        if (requestsList.containsKey(ipAddress)) {
            if (isInBlockedList(ipAddress)) {
                return false;
            }

            if (isAnExceedingRequest(ipAddress)) {
                return false;
            }

            requestsList.get(ipAddress).add(currentTime);
        } else {
            ArrayList<Calendar> calendarList = new ArrayList<Calendar>();
            calendarList.add(currentTime);

            requestsList.put(ipAddress, calendarList);
        }

        return true;
    }

    public Boolean isAnExceedingRequest(String ipAddress) {

        ArrayList<Calendar> requestsListArray = requestsList.get(ipAddress);
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
            blockedList.put(ipAddress, currentTime);

            return true;
        }

        return false;
    }

    public Boolean isInBlockedList(String ipAddress) {
        Calendar currentTime = getCurrentTime();
        Calendar blockTime = blockedList.get(ipAddress);

        if (blockTime == null) {
            return false;
        }

        if ((currentTime.getTimeInMillis() - blockTime.getTimeInMillis()) < BLOCKED_TIME) {
            return true;
        }

        blockedList.remove(ipAddress);

        return false;
    }

    public Calendar getCurrentTime() {
        Calendar now = Calendar.getInstance();
        return now;
    }

}
