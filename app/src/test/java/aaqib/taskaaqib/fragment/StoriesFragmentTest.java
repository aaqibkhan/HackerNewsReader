package aaqib.taskaaqib.fragment;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

@SmallTest
public class StoriesFragmentTest {

    /**
     * Check if getType() returns proper output
     */
    @Test
    public void getType_shouldReturnBest() {
        assertThat(StoriesFragment.getType(2), is("BEST"));
    }

    /**
     * Check if getType() handles out of range values
     */
    @Test
    public void getType_shouldReturnUnknown() {
        assertThat(StoriesFragment.getType(12), is("UNKNOWN"));
    }

}
