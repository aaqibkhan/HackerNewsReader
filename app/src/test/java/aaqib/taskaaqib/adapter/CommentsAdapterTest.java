package aaqib.taskaaqib.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

import aaqib.taskaaqib.models.Item;

@SmallTest
public class CommentsAdapterTest {

    /**
     * Verify whether setComment() actually sets the Item data
     */
    @Test
    public void verify_setComment() {
        CommentsAdapter commentsAdapter = new CommentsAdapter();
        Item item = new Item();
        item.setId(11737579L);
        commentsAdapter.setComment(item);
        assertThat(commentsAdapter.getItems().get(11737579L).getId(), is(11737579L));
    }

}
