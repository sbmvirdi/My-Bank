package cf.projectspro.bank;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import cf.projectspro.bank.repository.MyBankRepo;
import cf.projectspro.bank.ui.modelClasses.User;

@RunWith(JUnit4.class)
public class Transaction {

    User sender = new User("Shubam Virdi","abc",100);
    User receiver = new User("Harshit","bcd",100);
    int amountToBeSent = 10;
    int expectedAmount = 90;

    @Test
    public void verifyDeductions(){
        MyBankRepo.getInstance().updateUserAmounts(sender,receiver,10);
        if (sender.amount == expectedAmount){
            Assert.assertTrue(true);
        }else {
            Assert.fail("Amount invalid");
        }
    }
}
