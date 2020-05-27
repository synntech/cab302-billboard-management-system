package Shared;
import Shared.*;
import java.util.ArrayList;

public class TestCase {
    public static ArrayList<User> users () {
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Integer> perms = new ArrayList<>();
        perms.add(0,1);
        perms.add(1,1);
        perms.add(2,1);
        perms.add(3,1);
        users.add(new User("admin", "admin",perms, 0, "test"));
        perms = new ArrayList<>();
        perms.add(0,1);
        perms.add(1,1);
        perms.add(2,0);
        perms.add(3,0);
        users.add(new User("user1", "user1pass",perms, 0, "test"));
        perms = new ArrayList<>();
        perms.add(0,0);
        perms.add(1,0);
        perms.add(2,0);
        perms.add(3,0);
        users.add(new User("user2", "pass",perms, 0, "test"));
        return users;
    }

    public static ArrayList<Billboard> billboards () {
        ArrayList<Billboard> billboards = new ArrayList<>();
        billboards.add(new Billboard("creatorName", "name", "imageUrl", "msgText", "", "#000FFF", "infoText", ""));
        billboards.add(new Billboard("Harry", "Test", "NA", "This is the main test", "", "", "info text", ""));
        return billboards;
    }

    public static ArrayList<Scheduled> schedule () {
        ArrayList<Scheduled> schedule = new ArrayList<>();
        Scheduled newschedule = new Scheduled(1,1,ScheduleHelper.CalculateStart(0,5,0,1),5, new int[]{3,2,20});
        newschedule.setID(1);
        schedule.add(newschedule);



        return schedule;
    }
}
