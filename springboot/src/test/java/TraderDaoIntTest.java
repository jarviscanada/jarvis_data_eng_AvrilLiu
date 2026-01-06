import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.Trader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ca.jrvs.apps.trading.Application.class)
public class TraderDaoIntTest {

  @Autowired
  private TraderDao traderDao;

  @Test
  public void save_and_findById() {
    Trader t = new Trader();
    t.setFirstName("Test");
    t.setLastName("User");
    t.setDob(java.time.LocalDate.of(1990, 1, 1));
    t.setCountry("CA");
    t.setEmail("test@user.com");

    // 1. insert
    Trader saved = traderDao.save(t);

    // 2. id must be generated
    assertNotNull(saved.getId());

    // 3. read back from DB
    Trader found = traderDao.findById(saved.getId());
    assertEquals("Test", found.getFirstName());
    assertEquals("User", found.getLastName());
  }
}
