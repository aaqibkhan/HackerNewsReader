package aaqib.taskaaqib.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

import aaqib.taskaaqib.models.Item;

@SmallTest
public class StoriesAdapterTest {

    /**
     * Verify whether setStory() actually sets the Item data
     */
    @Test
    public void verify_setStory() {
        StoriesAdapter storiesAdapter = new StoriesAdapter();
        Item item = new Item();
        item.setId(11737579L);
        storiesAdapter.setStory(item);
        assertThat(storiesAdapter.getItems().get(11737579L).getId(), is(11737579L));
    }

}
