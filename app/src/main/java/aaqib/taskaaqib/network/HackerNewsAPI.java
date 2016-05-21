package aaqib.taskaaqib.network;

/**
 * This contains all the required APIs of HackerNews
 */
@SuppressWarnings("FieldCanBeLocal")
public class HackerNewsAPI {

    private static String API_TOP_STORIES = "https://hacker-news.firebaseio.com/v0/topstories.json";
    private static String API_NEW_STORIES = "https://hacker-news.firebaseio.com/v0/newstories.json";
    private static String API_BEST_STORIES = "https://hacker-news.firebaseio.com/v0/beststories.json";
    private static String API_ASK_STORIES = "https://hacker-news.firebaseio.com/v0/askstories.json";
    private static String API_SHOW_STORIES = "https://hacker-news.firebaseio.com/v0/showstories.json";
    private static String API_JOB_STORIES = "https://hacker-news.firebaseio.com/v0/jobstories.json";

    private static String API_ITEM = "https://hacker-news.firebaseio.com/v0/item/";

    public static String getApiTopStories() {
        return API_TOP_STORIES;
    }

    public static String getApiNewStories() {
        return API_NEW_STORIES;
    }

    public static String getApiBestStories() {
        return API_BEST_STORIES;
    }

    public static String getApiAskStories() {
        return API_ASK_STORIES;
    }

    public static String getApiShowStories() {
        return API_SHOW_STORIES;
    }

    public static String getApiJobStories() {
        return API_JOB_STORIES;
    }

    public static String getApiItem(String itemID) {
        return API_ITEM + itemID + ".json";
    }
}
