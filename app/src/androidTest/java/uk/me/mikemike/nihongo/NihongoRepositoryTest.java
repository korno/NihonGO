package uk.me.mikemike.nihongo;

import org.junit.Test;

import uk.me.mikemike.nihongo.data.NihongoRepository;

import static junit.framework.Assert.assertEquals;

/**
 * Created by mike on 11/29/17.
 */

public class NihongoRepositoryTest extends BaseTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullRealmTest(){
        NihongoRepository repos = new NihongoRepository(null);
    }

    @Test
    public void getAllDecksNoDecksTest(){
        NihongoRepository repos = new NihongoRepository(mRealm);
        assertEquals(0, repos.getAllDecks().size());
    }

}
