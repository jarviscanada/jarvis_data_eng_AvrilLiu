package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.Account;
import ca.jrvs.apps.trading.model.Position;
import ca.jrvs.apps.trading.model.Trader;
import ca.jrvs.apps.trading.view.TraderAccountView;
import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TraderAccountService {

  private final TraderDao traderDao;
  private final AccountDao accountDao;
  private final PositionDao positionDao;
  private final SecurityOrderDao securityOrderDao;

  public TraderAccountService(TraderDao traderDao,
      AccountDao accountDao,
      PositionDao positionDao,
      SecurityOrderDao securityOrderDao) {
    this.traderDao = traderDao;
    this.accountDao = accountDao;
    this.positionDao = positionDao;
    this.securityOrderDao = securityOrderDao;
  }

  @Transactional
  public TraderAccountView createTraderAndAccount(Trader trader){
    // 1) validate input
    if (trader == null) {
      throw new IllegalArgumentException("trader must not be null");
    }
    if (trader.getId() != null) {
      throw new IllegalArgumentException("trader.id must be null (auto-generated)");
    }
    validateNonEmpty(trader.getFirstName(), "firstName");
    validateNonEmpty(trader.getLastName(), "lastName");
    validateNonEmpty(trader.getCountry(), "country");
    validateNonEmpty(trader.getEmail(), "email");
    if (trader.getDob() == null) {
      throw new IllegalArgumentException("dob must not be null");
    }

    // 2) create trader (db generates trader.id)
    Trader savedTrader = traderDao.save(trader);
    if (savedTrader.getId() == null) {
      throw new IllegalStateException("failed to create trader (id is null)");
    }

    // 3) create account with 0 balance
    //    IMPORTANT: to satisfy assumption traderId == accountId, we insert account.id explicitly
    Account account = new Account();
    account.setId(savedTrader.getId());
    account.setTraderId(savedTrader.getId());
    account.setAmount(0.0);

    Account savedAccount = accountDao.insertWithId(account);

    // 4) build view
    TraderAccountView view = new TraderAccountView();
    view.setTrader(savedTrader);
    view.setAccount(savedAccount);
    view.setPositions(Collections.emptyList());

    return view;
  }

  /**
   * A trader can be deleted if and only if it has no open position and 0 cash balance
   * - validate traderId
   * - get trader account by traderId and check account balance
   * - get positions by accountId and check positions
   * - delete all securityOrders, account, trader (in this order)
   *
   * @param traderId must not be null
   * @throws IllegalArgumentException if traderId is null or not found or unable to delete
   */
  public void deleteTraderById(Integer traderId) {
    if (traderId == null) {
      throw new IllegalArgumentException("traderId must not be null");
    }

    Trader trader = traderDao.findById(traderId);
    if (trader == null) {
      throw new IllegalStateException("failed to find trader with id " + traderId);
    }

    Account account = accountDao.findById(trader.getId());
    if (account == null) {
      throw new IllegalStateException("failed to find account with trader id " + traderId);
    }

    if (account.getAmount()!=0.0){
      throw new IllegalStateException("unable to delete: account amount must be zero");
    }

    List<Position> positions = positionDao.findAllByAccountId(Collections.singletonList(account.getId()));
    for (Position p : positions) {
      if (p.getPosition() != null && p.getPosition() != 0L) {
        throw new IllegalArgumentException("unable to delete: open position exists");
      }
    }

    securityOrderDao.deleteById(account.getId());
    accountDao.deleteById(account.getId());
    traderDao.deleteById(traderId);
  }

  /**
   * Deposit fund to an account by traderId.
   */
  @Transactional
  public Account deposit(Integer traderId, Double fund) {
    // 1) validate input
    if (traderId == null) {
      throw new IllegalArgumentException("traderId must not be null");
    }
    if (fund == null || fund <= 0) {
      throw new IllegalArgumentException("fund must be > 0");
    }

    // 2) find account (assumption: accountId == traderId)
    Account account = accountDao.findById(traderId);
    if (account == null) {
      throw new IllegalArgumentException("account not found for traderId: " + traderId);
    }

    // 3) update amount and persist
    account.setAmount(account.getAmount() + fund);
    return accountDao.save(account);
  }

  /**
   * Withdraw fund from an account by traderId.
   */
  @Transactional
  public Account withdraw(Integer traderId, Double fund) {
    // 1) validate input
    if (traderId == null) {
      throw new IllegalArgumentException("traderId must not be null");
    }
    if (fund == null || fund <= 0) {
      throw new IllegalArgumentException("fund must be > 0");
    }

    // 2) find account (assumption: accountId == traderId)
    Account account = accountDao.findById(traderId);
    if (account == null) {
      throw new IllegalArgumentException("account not found for traderId: " + traderId);
    }

    // 3) check balance
    if (account.getAmount() < fund) {
      throw new IllegalArgumentException("insufficient balance");
    }

    // 4) update amount and persist
    account.setAmount(account.getAmount() - fund);
    return accountDao.save(account);
  }

  private void validateNonEmpty(String value, String fieldName) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(fieldName + " must not be empty");
    }
  }
}
