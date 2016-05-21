package aaqib.taskaaqib.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.Resources;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import aaqib.taskaaqib.R;

/**
 * Tests our App's utility class
 */
@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class AppUtilTest {

    private static final String SAMPLE_STRING = "This is a test string";
    private static final String JUST_NOW = "Just now";
    private static final String FOUR_SECONDS_AGO = "4 seconds ago";
    private static final String FOUR_MINUTES_AGO = "4 minutes ago";
    private static final String FOUR_HOURS_AGO = "4 hours ago";
    private static final String FOUR_DAYS_AGO = "4 days ago";
    private static final String TIME_FORMATTED = "21 May 2016 10:33:54 AM";
    private static final long TIME = 1463807034272L;
    private static final long TIME_4_SECONDS_MORE = 1463807038272L;
    private static final long TIME_4_MINUTES_MORE = 1463807300000L;
    private static final long TIME_4_HOURS_MORE = 1463821434272L;
    private static final long TIME_4_DAYS_MORE = 1464152634272L;
    private static final long TIME_4_WEEKS_MORE = 1466226234272L;

    @Mock
    Context mMockContext;
    @Mock
    Resources mMockResources;

    /**
     * Initialize Mockito's Mock objects and
     * define what they do and when
     */
    @Before
    public void initMocks() {
        when(mMockContext.getString(R.string.time_just_now)).thenReturn(JUST_NOW);
        when(mMockContext.getResources()).thenReturn(mMockResources);
        when(mMockResources.getQuantityString(R.plurals.time_seconds, 4, 4)).thenReturn(FOUR_SECONDS_AGO);
        when(mMockResources.getQuantityString(R.plurals.time_minutes, 4, 4)).thenReturn(FOUR_MINUTES_AGO);
        when(mMockResources.getQuantityString(R.plurals.time_hours, 4, 4)).thenReturn(FOUR_HOURS_AGO);
        when(mMockResources.getQuantityString(R.plurals.time_days, 4, 4)).thenReturn(FOUR_DAYS_AGO);
    }

    /**
     * Check if readStream() reads the InputStream properly
     */
    @Test
    public void readStream_shouldReturnSampleString() {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_STRING.getBytes());
        assertThat(AppUtil.readStream(inputStream), is(SAMPLE_STRING));
        assertThat(AppUtil.readStream(inputStream), is(""));
    }

    /**
     * Check if readStream() handles failures
     */
    @Test
    public void readStream_shouldReturnEmptyString() {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_STRING.getBytes());
        AppUtil.readStream(inputStream);
        assertThat(AppUtil.readStream(inputStream), is(""));
    }

    /**
     * Check if getItemTime() returns 'Just now' for same time
     */
    @Test
    public void getItemTime_shouldReturnJustNowString() {
        assertThat(AppUtil.getItemTime(mMockContext, TIME, TIME), is(JUST_NOW));
    }

    /**
     * Check if getItemTime() returns '4 seconds ago' for a time
     * which is 4 seconds behind current time
     */
    @Test
    public void getItemTime_shouldReturnFourSecondsAgoString() {
        assertThat(AppUtil.getItemTime(mMockContext, TIME_4_SECONDS_MORE, TIME), is(FOUR_SECONDS_AGO));
    }

    /**
     * Check if getItemTime() returns '4 minutes ago' for a time
     * which is 4 minutes behind current time
     */
    @Test
    public void getItemTime_shouldReturnFourMinutesAgoString() {
        assertThat(AppUtil.getItemTime(mMockContext, TIME_4_MINUTES_MORE, TIME), is(FOUR_MINUTES_AGO));
    }

    /**
     * Check if getItemTime() returns '4 hours ago' for a time
     * which is 4 hours behind current time
     */
    @Test
    public void getItemTime_shouldReturnFourHoursAgoString() {
        assertThat(AppUtil.getItemTime(mMockContext, TIME_4_HOURS_MORE, TIME), is(FOUR_HOURS_AGO));
    }

    /**
     * Check if getItemTime() returns '4 days ago' for a time
     * which is 4 days behind current time
     */
    @Test
    public void getItemTime_shouldReturnFourDaysAgoString() {
        assertThat(AppUtil.getItemTime(mMockContext, TIME_4_DAYS_MORE, TIME), is(FOUR_DAYS_AGO));
    }

    /**
     * Check if getItemTime() returns formatted time for a time
     * which is more than a week behind current time
     */
    @Test
    public void getItemTime_shouldReturnFormattedTime() {
        assertThat(AppUtil.getItemTime(mMockContext, TIME_4_WEEKS_MORE, TIME), is(TIME_FORMATTED));
    }

    /**
     * Check if getItemIDFromURL() returns valid ID for a valid input
     */
    @Test
    public void getItemIDFromURL_shouldReturnValidItemID() {
        String url = "https://hacker-news.firebaseio.com/v0/item/121003.json";
        assertThat(AppUtil.getItemIDFromURL(url), is(121003L));
    }

    /**
     * Check if getItemIDFromURL() handles invalid input
     */
    @Test
    public void getItemIDFromURL_shouldReturnInvalidItemID() {
        String url = "https://hacker-news.firebaseio.com/v0/item/121003.html";
        assertThat(AppUtil.getItemIDFromURL(url), is(-1L));
    }

}
