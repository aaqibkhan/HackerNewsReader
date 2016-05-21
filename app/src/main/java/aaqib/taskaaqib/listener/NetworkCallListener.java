package aaqib.taskaaqib.listener;


import aaqib.taskaaqib.models.ApiResponse;

/**
 * Used to notify stages of a network call
 */
public interface NetworkCallListener {

    /**
     * Called when a network call is about to initiate
     */
    void onCallStart();

    /**
     * Called after a network call has finished
     *
     * @param apiResponse The response of the network call {@link ApiResponse}
     */
    void onResponse(ApiResponse apiResponse);

}
